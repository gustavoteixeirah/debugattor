
package com.teixeirah.debugattor.domain.execution;

import java.util.UUID;
import java.time.Instant;
import java.util.Optional;

public record Execution(UUID id, Instant startedAt, Optional<Instant> finishedAt) {
}
