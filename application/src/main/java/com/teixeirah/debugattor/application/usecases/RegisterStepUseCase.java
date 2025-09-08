package com.teixeirah.debugattor.application.usecases;

import com.teixeirah.debugattor.domain.step.StepRepository;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import static com.teixeirah.debugattor.domain.step.Step.newStep;

@RequiredArgsConstructor
public class RegisterStepUseCase {

    private final StepRepository repository;

    public void execute(UUID id, String name) {

        repository.register(id, newStep(name));
    }

}
