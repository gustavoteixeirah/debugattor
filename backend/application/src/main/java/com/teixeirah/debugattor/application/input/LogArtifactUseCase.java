package com.teixeirah.debugattor.application.input;

import com.teixeirah.debugattor.domain.artifact.Artifact;
import com.teixeirah.debugattor.domain.artifact.FileMetadata;

import java.io.InputStream;
import java.util.UUID;

public interface LogArtifactUseCase {

    Artifact log(UUID stepId, Artifact.Type type, String description, String content);

    Artifact logFile(UUID stepId, Artifact.Type type, String description, InputStream file, FileMetadata metadata);
}
