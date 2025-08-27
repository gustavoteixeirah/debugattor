package br.com.studybuddy.debugattor.infrastructure.secondary.database;


import br.com.studybuddy.debugattor.domain.execution.Execution;
import br.com.studybuddy.debugattor.domain.execution.ExecutionRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
class ExecutionRepositoryPostgresAdapter implements ExecutionRepository {

    private final HashMap<UUID, Execution> executions = new HashMap<>();

    @Override
    public Execution create() {
        UUID id = UUID.randomUUID();
        final var execution = new Execution(id, java.time.Instant.now(), java.util.Optional.empty());
        executions.put(id, execution);
        return execution;
    }

    @Override
    public List<Execution> findAll() {
        return executions.values().stream().toList();
    }

    @Override
    public Optional<Execution> findById(UUID id) {
        return Optional.ofNullable(executions.get(id));
    }

}