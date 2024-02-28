package com.ahmadabbas.filetracking.backend.util;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.models.BlobItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AzureBlobService {

    private final BlobContainerClient blobContainerClient;

    public String upload(File file, String path) throws IOException {
        if (file == null || !file.isFile()) {
            throw new RuntimeException("failed to upload");
        }
        String uuid = UUID.randomUUID().toString();

        String fullPath = path + "/" + uuid + FileNameUtil.getFileExtension(file.getName());
        BlobClient blob = blobContainerClient.getBlobClient(fullPath);

        FileInputStream inputStream = new FileInputStream(file);
        blob.upload(inputStream, file.length(), true);
        return fullPath;
    }

    public String upload(MultipartFile multipartFile, String path) throws IOException {
        if (multipartFile == null || multipartFile.getOriginalFilename() == null) {
            throw new RuntimeException("failed to upload");
        }
        String uuid = UUID.randomUUID().toString();

        String fullPath = path + "/" + uuid + FileNameUtil.getFileExtension(multipartFile.getOriginalFilename());
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
        List<String> names = new ArrayList<>();
        for (BlobItem item : items) {
            names.add(item.getName());
        }
        return names;
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
}