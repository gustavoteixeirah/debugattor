package com.teixeirah.debugattor.application.input;

import com.teixeirah.debugattor.domain.execution.Execution;

import java.util.Optional;
import java.util.UUID;

public interface GetExecutionByIdUseCase {

    Optional<Execution> execute(UUID id);
}
