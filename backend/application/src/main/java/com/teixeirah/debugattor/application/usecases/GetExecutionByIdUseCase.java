package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.domain.execution.Execution;
import com.teixeirah.debugattor.domain.execution.ExecutionRepository;

import java.util.Optional;
import java.util.UUID;

public class GetExecutionByIdUseCase {

    private final ExecutionRepository repository;

    public GetExecutionByIdUseCase(ExecutionRepository executionRepository) {
        this.repository = executionRepository;
    }

    public Optional<Execution> execute(UUID id) {
        return repository.findById(id);
    }
}
