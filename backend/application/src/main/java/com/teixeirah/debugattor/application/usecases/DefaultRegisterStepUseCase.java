package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.application.input.RegisterStepUseCase;
import com.teixeirah.debugattor.domain.events.EventPublisher;
import com.teixeirah.debugattor.domain.events.StepRegisteredEvent;
import com.teixeirah.debugattor.domain.step.Step;
import com.teixeirah.debugattor.domain.step.StepRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static com.teixeirah.debugattor.domain.step.Step.newStep;

@RequiredArgsConstructor
public class DefaultRegisterStepUseCase implements RegisterStepUseCase {

    private final StepRepository repository;
    private final EventPublisher eventPublisher;

    @Override
    public Step execute(UUID id, String name) {
        Step step = repository.register(id, newStep(name));
        eventPublisher.publish(new StepRegisteredEvent(id, step.id(), step.name(), step.name()));
        return step;
    }
}
