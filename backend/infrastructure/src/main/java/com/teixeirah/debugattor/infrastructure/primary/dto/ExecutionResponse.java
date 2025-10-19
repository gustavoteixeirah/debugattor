package com.teixeirah.debugattor.infrastructure.primary.dto;

import com.teixeirah.debugattor.domain.execution.Execution;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ExecutionResponse(
        UUID id,
        OffsetDateTime startedAt,
        OffsetDateTime finishedAt,
        List<StepResponse> steps
) {
    public static ExecutionResponse from(Execution execution) {
        List<StepResponse> stepResponses = execution.steps() == null
                ? List.of()
                : execution.steps().stream().map(StepResponse::from).toList();
        return new ExecutionResponse(
                execution.id(),
                execution.startedAt(),
                execution.finishedAt(),
                stepResponses
        );
    }
}
