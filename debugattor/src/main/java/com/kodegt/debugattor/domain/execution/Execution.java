
package com.kodegt.debugattor.domain.execution;

import com.kodegt.debugattor.domain.step.Step;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record Execution(UUID id, Status status, List<Step> steps, OffsetDateTime startedAt, OffsetDateTime finishedAt) {

    public String duration() {
        if (startedAt == null || finishedAt == null) return "";

        Duration duration = Duration.between(startedAt, finishedAt);
        long millis = duration.toMillis();
        if (millis < 1000) {
            return millis + "ms";
        }
        
        long seconds = duration.toSeconds();
        if (seconds < 60) {
            return seconds + "s";
        }

        long minutes = duration.toMinutes();
        if (minutes < 60) {
            return minutes + "m " + (seconds % 60) + "s";
        }

        return minutes / 60 + "h " + (minutes % 60) + "m";
    }

    public enum Status {
        RUNNING,
        COMPLETED,
        FAILED
    }
}
