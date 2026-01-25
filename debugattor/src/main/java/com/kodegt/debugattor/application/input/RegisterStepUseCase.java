package com.kodegt.debugattor.application.input;

import com.kodegt.debugattor.domain.step.Step;

import java.util.UUID;

public interface RegisterStepUseCase {

    Step execute(UUID id, String name);
}
