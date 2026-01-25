package com.kodegt.debugattor.application.input;

import com.kodegt.debugattor.domain.execution.Execution;

import java.util.Optional;
import java.util.UUID;

public interface GetExecutionByIdUseCase {

    Optional<Execution> execute(UUID id);
}
