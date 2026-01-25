package com.kodegt.debugattor.application.usecases;

import com.kodegt.debugattor.application.input.LogArtifactUseCase;
import com.kodegt.debugattor.domain.artifact.Artifact;
import com.kodegt.debugattor.domain.artifact.ArtifactRepository;
import com.kodegt.debugattor.domain.artifact.FileMetadata;
import com.kodegt.debugattor.domain.events.ArtifactLoggedEvent;
import com.kodegt.debugattor.domain.events.EventPublisher;
import com.kodegt.debugattor.domain.storage.BucketStorageOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefaultLogArtifactUseCase implements LogArtifactUseCase {

    private final ArtifactRepository artifactRepository;
    private final BucketStorageOutputPort bucketStorageOutputPort;
    private final EventPublisher eventPublisher;

    @Override
    public Artifact log(UUID stepId, Artifact.Type type, String description, String content) {
        Artifact artifact = artifactRepository.log(stepId, type, description, content);
        eventPublisher.publish(new ArtifactLoggedEvent(stepId, artifact.id(), artifact.type().name(), artifact.description(), artifact.content(), artifact.content()));
        return artifact;
    }

    @Override
    public Artifact logFile(UUID stepId, Artifact.Type type, String description, InputStream file, FileMetadata metadata) {
        var artifact = artifactRepository.createWithoutUrl(stepId, type, description);
        final var url = bucketStorageOutputPort.storeFile(file, artifact.id().toString(), metadata.contentType(), metadata.size());
        artifactRepository.updateContent(artifact.id(), url);
        Artifact completedArtifact = new Artifact(
                artifact.id(),
                artifact.type(),
                artifact.description(),
                url,
                artifact.loggedAt()
        );
        eventPublisher.publish(new ArtifactLoggedEvent(stepId, completedArtifact.id(), completedArtifact.type().name(), completedArtifact.description(), completedArtifact.content(), completedArtifact.content()));
        return completedArtifact;
    }
}
