package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.application.input.CompleteExecutionUseCase;
import com.teixeirah.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class DefaultCompleteExecutionUseCase implements CompleteExecutionUseCase {

    private final ExecutionRepository repository;

    @Override
    public void execute(UUID id) {
        repository.complete(id);
    }
}
