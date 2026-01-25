package com.kodegt.debugattor.application.usecases;

import com.kodegt.debugattor.application.input.RegisterStepUseCase;
import com.kodegt.debugattor.domain.events.EventPublisher;
import com.kodegt.debugattor.domain.events.StepRegisteredEvent;
import com.kodegt.debugattor.domain.step.Step;
import com.kodegt.debugattor.domain.step.StepRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.kodegt.debugattor.domain.step.Step.newStep;

@Component
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
