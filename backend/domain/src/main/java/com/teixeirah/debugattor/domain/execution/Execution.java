
package com.teixeirah.debugattor.domain.execution;

import com.teixeirah.debugattor.domain.step.Step;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.time.Instant;
import java.util.Optional;

public record Execution(UUID id, List<Step> steps, OffsetDateTime startedAt, OffsetDateTime finishedAt) {
}
