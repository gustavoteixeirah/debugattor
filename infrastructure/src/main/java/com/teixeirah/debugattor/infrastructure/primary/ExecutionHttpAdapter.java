package com.teixeirah.debugattor.infrastructure.primary;

import com.teixeirah.debugattor.application.usecases.FetchExecutionsUseCase;
import com.teixeirah.debugattor.application.usecases.GetExecutionByIdUseCase;
import com.teixeirah.debugattor.application.usecases.RegisterStepUseCase;
import com.teixeirah.debugattor.application.usecases.StartExecutionUseCase;
import com.teixeirah.debugattor.domain.execution.Execution;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/executions")
class ExecutionHttpAdapter {

    private final StartExecutionUseCase startExecutionUseCase;
    private final FetchExecutionsUseCase fetchExecutionsUseCase;
    private final GetExecutionByIdUseCase getExecutionByIdUseCase;
    private final RegisterStepUseCase registerStepUseCase;

    @PostMapping
    ResponseEntity<Execution> startExecution() {
        return ResponseEntity.ok(startExecutionUseCase.execute());
    }

    @GetMapping
    ResponseEntity<List<Execution>> fetchExecutions() {
        List<Execution> executions = fetchExecutionsUseCase.execute();
        return ResponseEntity.ok(executions);
    }

    @GetMapping("/{executionId}")
    ResponseEntity<Execution> getExecutionById(@PathVariable UUID executionId) {
        return getExecutionByIdUseCase.execute(executionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{executionId}/steps")
    ResponseEntity<Execution> registerStep(@PathVariable UUID executionId, @RequestBody RegisterStepDto registerStepDto) {
        registerStepUseCase.execute(executionId, registerStepDto.name());
        return getExecutionById(executionId);
    }

    public record RegisterStepDto(String name) {
    }

}
