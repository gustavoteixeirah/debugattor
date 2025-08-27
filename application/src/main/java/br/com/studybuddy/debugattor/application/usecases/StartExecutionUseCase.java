package br.com.studybuddy.debugattor.application.usecases;

import br.com.studybuddy.debugattor.domain.execution.Execution;
import br.com.studybuddy.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class StartExecutionUseCase {

    private final ExecutionRepository repository;

    public Execution execute() {
        return repository.create();
    }
}
