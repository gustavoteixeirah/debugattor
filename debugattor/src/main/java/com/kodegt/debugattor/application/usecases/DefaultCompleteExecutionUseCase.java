package com.kodegt.debugattor.application.usecases;

import com.kodegt.debugattor.application.input.CompleteExecutionUseCase;
import com.kodegt.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefaultCompleteExecutionUseCase implements CompleteExecutionUseCase {

    private final ExecutionRepository repository;

    @Override
    public void execute(UUID id) {
        repository.complete(id);
    }
}
