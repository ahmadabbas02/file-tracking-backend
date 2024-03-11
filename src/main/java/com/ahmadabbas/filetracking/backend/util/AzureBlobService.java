package com.ahmadabbas.filetracking.backend.util;

import com.ahmadabbas.filetracking.backend.exception.APIException;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class AzureBlobService {
    private final int maxFileRenameTries = 15;

    private final BlobContainerClient blobContainerClient;

    public String upload(File file, String parentFolder, String categoryName, String title) throws IOException {
        if (file == null || !file.isFile()) {
            throw new RuntimeException("failed to upload");
        }
        log.debug("AzureBlobService.upload");
        log.debug("file = " + file + ", parentFolder = " + parentFolder + ", categoryName = " + categoryName + ", title = " + title);
        String fullPath = getNonDuplicateFullPath(categoryName,
                title,
                file.getName(),
                parentFolder);
        log.debug("fullPath = " + fullPath);
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
        log.debug("AzureBlobService.upload");
        log.debug("multipartFile = " + multipartFile + ", parentFolder = " + parentFolder + ", categoryName = " + categoryName + ", title = " + title);
        String fullPath = getNonDuplicateFullPath(categoryName,
                title,
                multipartFile.getOriginalFilename(),
                parentFolder);
        log.debug("fullPath = " + fullPath);
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

    public InputStream getInputStream(String blobName) {
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        return blobClient.downloadContent().toStream();
    }

    private String getNonDuplicateFullPath(String categoryName,
                                           String title,
                                           String originalFileName,
                                           String parentFolder) {
        String fileNameWithoutExtension = "%s-%s"
                .formatted(categoryName, title);
        String fileName = "%s%s"
                .formatted(fileNameWithoutExtension,
                        FileNameUtil.getFileExtension(originalFileName));
        List<String> blobsInParentFolder = getBlobsInParentFolder(parentFolder);
        blobsInParentFolder = blobsInParentFolder
                .stream()
                .map(name ->
                        name.replaceAll(parentFolder + "/", "")
                                .replace(FileNameUtil.getFileExtension(name), "")
                )
                .toList();
        log.debug("blobsInParentFolder = " + blobsInParentFolder);

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
                            FileNameUtil.getFileExtension(originalFileName));
            count++;
        }
        return parentFolder + "/" + fileName;
    }
}