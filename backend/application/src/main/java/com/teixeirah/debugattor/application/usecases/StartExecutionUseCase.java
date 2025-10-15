package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.domain.execution.Execution;
import com.teixeirah.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartExecutionUseCase {

    private final ExecutionRepository repository;

    public Execution execute() {
        return repository.create();
    }
}
