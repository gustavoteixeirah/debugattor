package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.application.input.FetchExecutionsUseCase;
import com.teixeirah.debugattor.domain.execution.Execution;
import com.teixeirah.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DefaultFetchExecutionsUseCase implements FetchExecutionsUseCase {

    private final ExecutionRepository repository;

    @Override
    public List<Execution> execute() {
        return repository.findAll();
    }
}
