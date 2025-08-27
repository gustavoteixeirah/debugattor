package br.com.studybuddy.debugattor.application.usecases;

import br.com.studybuddy.debugattor.domain.execution.Execution;
import br.com.studybuddy.debugattor.domain.execution.ExecutionRepository;

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
