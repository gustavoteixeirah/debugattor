package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.domain.execution.Execution;
import com.teixeirah.debugattor.domain.execution.ExecutionRepository;

import java.util.List;

public class FetchExecutionsUseCase {
    private final ExecutionRepository repository;

    public FetchExecutionsUseCase(ExecutionRepository executionRepository) {
        this.repository = executionRepository;
    }

    public List<Execution> execute() {
        return repository.findAll();
    }
}
