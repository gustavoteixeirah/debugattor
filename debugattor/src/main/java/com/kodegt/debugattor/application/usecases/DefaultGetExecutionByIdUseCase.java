package com.kodegt.debugattor.application.usecases;

import com.kodegt.debugattor.application.input.GetExecutionByIdUseCase;
import com.kodegt.debugattor.domain.execution.Execution;
import com.kodegt.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefaultGetExecutionByIdUseCase implements GetExecutionByIdUseCase {

    private final ExecutionRepository repository;

    @Override
    public Optional<Execution> execute(UUID id) {
        return repository.findById(id);
    }
}
