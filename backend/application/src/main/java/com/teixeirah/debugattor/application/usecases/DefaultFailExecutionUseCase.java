package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.application.input.FailExecutionUseCase;
import com.teixeirah.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class DefaultFailExecutionUseCase implements FailExecutionUseCase {

    private final ExecutionRepository repository;

    @Override
    public void execute(UUID id) {
        repository.fail(id);
    }
}
