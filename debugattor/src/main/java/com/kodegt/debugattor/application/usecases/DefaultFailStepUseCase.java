package com.kodegt.debugattor.application.usecases;

import com.kodegt.debugattor.application.input.FailStepUseCase;
import com.kodegt.debugattor.domain.step.StepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefaultFailStepUseCase implements FailStepUseCase {

    private final StepRepository repository;

    @Override
    public void execute(UUID stepId) {
        repository.setFailed(stepId);
    }
}
