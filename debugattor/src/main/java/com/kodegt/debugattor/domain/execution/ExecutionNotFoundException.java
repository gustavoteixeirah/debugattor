package com.kodegt.debugattor.domain.execution;

import java.util.UUID;

public class ExecutionNotFoundException extends RuntimeException {
    private final UUID executionId;

    public ExecutionNotFoundException(UUID executionId) {
        super("Execution not found: " + executionId);
        this.executionId = executionId;
    }

    public UUID getExecutionId() {
        return executionId;
    }
}

