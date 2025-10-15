package com.teixeirah.debugattor.domain.step;

import java.util.UUID;

public class StepNotFoundException extends RuntimeException {
    private final UUID stepId;

    public StepNotFoundException(UUID stepId) {
        super("Step not found: " + stepId);
        this.stepId = stepId;
    }

    public UUID getStepId() {
        return stepId;
    }
}

