package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.domain.step.StepRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class CompleteStepUseCase {

    private final StepRepository repository;

    public void execute(UUID stepId) {
        repository.setCompleted(stepId);
    }

}