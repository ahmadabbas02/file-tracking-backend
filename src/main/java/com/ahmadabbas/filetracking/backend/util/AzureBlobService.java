package com.ahmadabbas.filetracking.backend.util;

import com.ahmadabbas.filetracking.backend.exception.APIException;
import com.ahmadabbas.filetracking.backend.exception.ResourceNotFoundException;
import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.ListBlobsOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AzureBlobService {
    private final int maxFileRenameTries = 15;

    private final BlobContainerClient blobContainerClient;

    public String upload(File file,
                         String parentFolder,
                         String categoryName,
                         String title) throws IOException {
        if (file == null || !file.isFile()) {
            throw new RuntimeException("failed to upload");
        }
        log.debug("file = {}, parentFolder = {}, categoryName = {}, title = {}", file, parentFolder, categoryName, title);
        String fullPath = getNonDuplicateFullPath(categoryName,
                title,
                file.getName(),
                parentFolder);
        BlobClient blob = blobContainerClient.getBlobClient(fullPath);

        FileInputStream inputStream = new FileInputStream(file);
        blob.upload(inputStream, file.length(), true);
        inputStream.close();
        return fullPath;
    }

    public String upload(MultipartFile multipartFile,
                         String parentFolder,
                         String categoryName,
                         String title) throws IOException {
        if (multipartFile == null || multipartFile.getOriginalFilename() == null) {
            throw new RuntimeException("failed to upload");
        }
        log.debug("multipartFile = {}, parentFolder = {}, categoryName = {}, title = {}", multipartFile, parentFolder, categoryName, title);
        String fullPath = getNonDuplicateFullPath(categoryName,
                title,
                multipartFile.getOriginalFilename(),
                parentFolder);
        BlobClient blob = blobContainerClient.getBlobClient(fullPath);
        blob.upload(multipartFile.getInputStream(), multipartFile.getSize(), true);
        return fullPath;
    }

    public byte[] getFile(String fileName) {
        BlobClient blob = blobContainerClient.getBlobClient(fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blob.downloadStream(outputStream);
        return outputStream.toByteArray();
    }

    public List<String> getBlobs() {
        PagedIterable<BlobItem> items = blobContainerClient.listBlobs();
        return items.stream().map(BlobItem::getName).toList();
    }

    public List<String> getBlobsInParentFolder(String parentFolder) {
        ListBlobsOptions options = new ListBlobsOptions()
                .setPrefix(parentFolder);
        PagedIterable<BlobItem> items = blobContainerClient.listBlobs(options, Duration.ofMinutes(1));
        return items.stream().map(BlobItem::getName).toList();
    }

    public Boolean deleteBlob(String blobName) {
        BlobClient blob = blobContainerClient.getBlobClient(blobName);
        blob.delete();
        return true;
    }

    public InputStream getInputStream(String blobName, UUID uuid) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        if (!blobClient.exists()) {
            throw new ResourceNotFoundException(
                    "requested file with id `%s` does not exist".formatted(uuid.toString())
            );
        }
        return blobClient.downloadContent().toStream();
    }

    private String getNonDuplicateFullPath(String categoryName,
                                           String title,
                                           String originalFileName,
                                           String parentFolder) {
        log.debug("categoryName = {}, title = {}, originalFileName = {}, parentFolder = {}", categoryName, title, originalFileName, parentFolder);
        title = FileNameUtils.sanitizeFileName(title);
        originalFileName = FileNameUtils.sanitizeFileName(originalFileName);
        log.debug("title = {}", title);
        log.debug("originalFileName = {}", originalFileName);
        String fileNameWithoutExtension = "%s-%s"
                .formatted(categoryName, title);
        String fileName = "%s%s"
                .formatted(fileNameWithoutExtension,
                        FileNameUtils.getFileExtension(originalFileName));
        List<String> blobsInParentFolder = getBlobsInParentFolder(parentFolder);
        blobsInParentFolder = blobsInParentFolder
                .stream()
                .map(name ->
                        name.replaceAll(parentFolder + "/", "")
                                .replace(FileNameUtils.getFileExtension(name), "")
                )
                .toList();
        int count = 1;
        while (blobsInParentFolder.contains(fileNameWithoutExtension)) {
            if (count >= maxFileRenameTries) {
                throw new APIException(
                        HttpStatus.BAD_REQUEST,
                        "A file already exists with name %s. %d maximum tries of file renaming failed."
                                .formatted(fileNameWithoutExtension, maxFileRenameTries)
                );
            }
            fileNameWithoutExtension = "%s-%s-%d"
                    .formatted(categoryName, title, count);
            fileName = "%s%s"
                    .formatted(fileNameWithoutExtension,
                            FileNameUtils.getFileExtension(originalFileName));
            count++;
        }
        return parentFolder + "/" + fileName;
    }
}