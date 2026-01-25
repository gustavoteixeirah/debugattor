package com.kodegt.debugattor.domain.execution;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExecutionRepository {
    Execution create();

    List<Execution> findAll();

    Optional<Execution> findById(UUID id);

    boolean deleteById(UUID id);

    void complete(UUID id);

    void fail(UUID id);

    List<Execution> findAll(Pageable pageable);

    List<Execution> findAll(String id, Pageable pageable);
}
