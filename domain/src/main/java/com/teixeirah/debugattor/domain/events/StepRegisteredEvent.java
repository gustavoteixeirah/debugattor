package com.teixeirah.debugattor.domain.events;

import java.util.UUID;

public record StepRegisteredEvent(UUID executionId, UUID stepId, String name, String description) {
}
