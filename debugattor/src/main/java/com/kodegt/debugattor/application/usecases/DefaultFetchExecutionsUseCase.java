package com.kodegt.debugattor.application.usecases;

import com.kodegt.debugattor.application.input.FetchExecutionsUseCase;
import com.kodegt.debugattor.domain.execution.Execution;
import com.kodegt.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DefaultFetchExecutionsUseCase implements FetchExecutionsUseCase {

    private final ExecutionRepository repository;

    @Override
    public List<Execution> execute() {
        return repository.findAll();
    }

    @Override
    public List<Execution> fetch(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<Execution> fetch(String id, Pageable pageable) {
        return repository.findAll(id, pageable);
    }
}
