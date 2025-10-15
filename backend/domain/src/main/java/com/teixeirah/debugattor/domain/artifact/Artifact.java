package com.teixeirah.debugattor.domain.artifact;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Artifact(UUID id, Artifact.Type type, String description, String content, OffsetDateTime loggedAt) {

    public enum Type {
        IMAGE, // content will be a base64 image
        LOG, // content will be just a text
        JSON_DATA // content will be a json object
    }

    public static Artifact newArtifact(UUID id, String type, String description, String content, OffsetDateTime loggedAt) {
        return new Artifact(id, Type.valueOf(type), description, content, loggedAt);
    }
}

