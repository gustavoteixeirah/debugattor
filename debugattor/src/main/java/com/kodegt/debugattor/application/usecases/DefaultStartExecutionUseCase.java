package com.kodegt.debugattor.application.usecases;

import com.kodegt.debugattor.application.input.StartExecutionUseCase;
import com.kodegt.debugattor.domain.execution.Execution;
import com.kodegt.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DefaultStartExecutionUseCase implements StartExecutionUseCase {

    private final ExecutionRepository repository;

    @Override
    public Execution execute() {
        return repository.create();
    }
}
