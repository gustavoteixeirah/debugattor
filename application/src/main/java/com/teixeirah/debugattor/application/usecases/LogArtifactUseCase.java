package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.application.output.BucketStorageOutputPort;
import com.teixeirah.debugattor.domain.artifact.Artifact;
import com.teixeirah.debugattor.domain.artifact.ArtifactRepository;
import com.teixeirah.debugattor.domain.artifact.FileMetadata;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.util.UUID;

@RequiredArgsConstructor
public class LogArtifactUseCase {

    private final ArtifactRepository artifactRepository;
    private final BucketStorageOutputPort bucketStorageOutputPort;

    public Artifact log(UUID stepId, Artifact.Type type, String description, String content) {
        return artifactRepository.log(stepId, type, description, content);
    }

    public Artifact logFile(UUID stepId, Artifact.Type type, String description, InputStream file, FileMetadata metadata) {
        var artifact = artifactRepository.createWithoutUrl(stepId, type, description);
        final var url = bucketStorageOutputPort.storeFile(file, artifact.id().toString(), metadata.contentType(), metadata.size());
        artifactRepository.updateContent(artifact.id(), url);

        return new Artifact(
                artifact.id(),
                artifact.type(),
                artifact.description(),
                url,
                artifact.loggedAt()
        );
    }

}
