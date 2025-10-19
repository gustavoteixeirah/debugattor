package com.teixeirah.debugattor.application.input;

import com.teixeirah.debugattor.domain.step.Step;

import java.util.UUID;

public interface RegisterStepUseCase {

    Step execute(UUID id, String name);
}
