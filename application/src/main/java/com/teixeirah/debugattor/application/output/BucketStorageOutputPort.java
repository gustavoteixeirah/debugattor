package com.teixeirah.debugattor.application.output;

import java.io.InputStream;
import java.util.Optional;

public interface BucketStorageOutputPort {

    String storeFile(InputStream fileStream, String objectName, String contentType, long fileSize);

    Optional<InputStream> getFile(String objectName);

    void deleteFile(String objectName);
}
