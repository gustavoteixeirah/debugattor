package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.application.input.CompleteStepUseCase;
import com.teixeirah.debugattor.domain.step.StepRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class DefaultCompleteStepUseCase implements CompleteStepUseCase {

    private final StepRepository repository;

    @Override
    public void execute(UUID stepId) {
        repository.setCompleted(stepId);
    }
}
