package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.application.input.DeleteExecutionUseCase;
import com.teixeirah.debugattor.domain.artifact.ArtifactRepository;
import com.teixeirah.debugattor.domain.execution.ExecutionRepository;
import com.teixeirah.debugattor.domain.storage.BucketStorageOutputPort;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class DefaultDeleteExecutionUseCase implements DeleteExecutionUseCase {

    private final ExecutionRepository executionRepository;
    private final ArtifactRepository artifactRepository;
    private final BucketStorageOutputPort bucketStorage;

    @Override
    public void delete(UUID executionId) {
        var imageUrls = artifactRepository.findImagesByExecutionId(executionId);
        for (final var url : imageUrls) {
            int lastSlash = url.lastIndexOf('/');
            if (lastSlash != -1) {
                final var objectName = url.substring(lastSlash + 1);
                bucketStorage.deleteFile(objectName);
            }
        }

        executionRepository.deleteById(executionId);
    }
}
