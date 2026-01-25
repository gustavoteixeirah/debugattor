package com.kodegt.debugattor.application.usecases;

import com.kodegt.debugattor.application.input.DeleteExecutionUseCase;
import com.kodegt.debugattor.domain.artifact.ArtifactRepository;
import com.kodegt.debugattor.domain.execution.ExecutionRepository;
import com.kodegt.debugattor.domain.storage.BucketStorageOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
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
