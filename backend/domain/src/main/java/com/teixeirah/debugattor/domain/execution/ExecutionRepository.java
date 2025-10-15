package com.teixeirah.debugattor.domain.execution;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExecutionRepository {
    Execution create();

    List<Execution> findAll();

    Optional<Execution> findById(UUID id);

    boolean deleteById(UUID id);
}
