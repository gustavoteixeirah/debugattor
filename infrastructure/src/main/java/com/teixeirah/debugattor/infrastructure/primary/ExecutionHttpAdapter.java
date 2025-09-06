package com.teixeirah.debugattor.infrastructure.primary;

import com.teixeirah.debugattor.application.usecases.FetchExecutionsUseCase;
import com.teixeirah.debugattor.application.usecases.GetExecutionByIdUseCase;
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

    @PostMapping
    ResponseEntity<Execution> startExecution() {
        return ResponseEntity.ok(startExecutionUseCase.execute());
    }

    @GetMapping
    ResponseEntity<List<Execution>> fetchExecutions() {
        return ResponseEntity.ok(fetchExecutionsUseCase.execute());
    }

    @GetMapping("/{id}")
    ResponseEntity<Execution> getExecutionById(@PathVariable UUID id) {
        return getExecutionByIdUseCase.execute(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
