package com.ahmadabbas.filetracking.backend.config;

import com.azure.storage.blob.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobConfig {

    @Value("${azure.storage.connection.string}")
    private String connectionString;

    @Value("${azure.storage.container.name}")
    private String containerName;

    @Bean
    public BlobServiceClient blobServiceClient() {
        return new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

    @Bean
    public BlobContainerClient blobContainerClient() {
        return blobServiceClient().getBlobContainerClient(containerName);
    }

}