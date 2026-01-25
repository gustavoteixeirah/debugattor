package com.kodegt.debugattor.application.usecases;

import com.kodegt.debugattor.application.input.CompleteStepUseCase;
import com.kodegt.debugattor.domain.step.StepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DefaultCompleteStepUseCase implements CompleteStepUseCase {

    private final StepRepository repository;

    @Override
    public void execute(UUID stepId) {
        repository.setCompleted(stepId);
    }
}
