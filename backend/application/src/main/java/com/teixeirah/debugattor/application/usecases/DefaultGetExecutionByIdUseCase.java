package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.application.input.GetExecutionByIdUseCase;
import com.teixeirah.debugattor.domain.execution.Execution;
import com.teixeirah.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class DefaultGetExecutionByIdUseCase implements GetExecutionByIdUseCase {

    private final ExecutionRepository repository;

    @Override
    public Optional<Execution> execute(UUID id) {
        return repository.findById(id);
    }
}
