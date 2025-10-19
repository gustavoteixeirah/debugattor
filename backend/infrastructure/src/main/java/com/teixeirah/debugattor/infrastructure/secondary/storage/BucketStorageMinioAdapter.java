package com.teixeirah.debugattor.infrastructure.secondary.storage;


import com.teixeirah.debugattor.domain.storage.BucketStorageOutputPort;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
class BucketStorageMinioAdapter implements BucketStorageOutputPort {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.publicUrl:${minio.url}}")
    private String publicUrl;

    @Override
    public String storeFile(InputStream fileStream, String objectName, String contentType, long fileSize) {
        log.info("Storing file {} to bucket {}", objectName, bucketName);
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(fileStream, fileSize, -1)
                            .contentType(contentType)
                            .build()
            );
            // Return a browser-accessible URL using publicUrl
            return String.format("%s/%s/%s", publicUrl, bucketName, objectName);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao fazer upload do arquivo para o MinIO: " + e.getMessage());
        }
    }


    @Override
    public Optional<InputStream> getFile(String objectName) {
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
            return Optional.of(stream);
        } catch (MinioException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get file from MinIO: " + e.getMessage());
        }
    }

    @Override
    public void deleteFile(String objectName) {
        log.info("Deleting file {} from bucket {}", objectName, bucketName);
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete file from MinIO: " + e.getMessage());
        }
    }
}