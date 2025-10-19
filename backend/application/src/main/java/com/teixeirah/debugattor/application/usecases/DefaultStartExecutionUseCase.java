package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.application.input.StartExecutionUseCase;
import com.teixeirah.debugattor.domain.execution.Execution;
import com.teixeirah.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultStartExecutionUseCase implements StartExecutionUseCase {

    private final ExecutionRepository repository;

    @Override
    public Execution execute() {
        return repository.create();
    }
}
