package br.com.studybuddy.debugattor.application.usecases;

import br.com.studybuddy.debugattor.domain.execution.Execution;
import br.com.studybuddy.debugattor.domain.execution.ExecutionRepository;

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
