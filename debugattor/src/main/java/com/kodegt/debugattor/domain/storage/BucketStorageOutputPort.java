package com.kodegt.debugattor.domain.storage;

import java.io.InputStream;
import java.util.Optional;

public interface BucketStorageOutputPort {

    String storeFile(InputStream fileStream, String objectName, String contentType, long fileSize);

    Optional<InputStream> getFile(String objectName);

    void deleteFile(String objectName);
}
