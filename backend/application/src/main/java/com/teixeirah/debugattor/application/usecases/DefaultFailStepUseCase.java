package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.application.input.FailStepUseCase;
import com.teixeirah.debugattor.domain.step.StepRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class DefaultFailStepUseCase implements FailStepUseCase {

    private final StepRepository repository;

    @Override
    public void execute(UUID stepId) {
        repository.setFailed(stepId);
    }
}
