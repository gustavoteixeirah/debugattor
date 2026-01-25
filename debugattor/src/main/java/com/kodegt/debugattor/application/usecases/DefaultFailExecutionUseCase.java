package com.kodegt.debugattor.application.usecases;

import com.kodegt.debugattor.application.input.FailExecutionUseCase;
import com.kodegt.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefaultFailExecutionUseCase implements FailExecutionUseCase {

    private final ExecutionRepository repository;

    @Override
    public void execute(UUID id) {
        repository.fail(id);
    }
}
