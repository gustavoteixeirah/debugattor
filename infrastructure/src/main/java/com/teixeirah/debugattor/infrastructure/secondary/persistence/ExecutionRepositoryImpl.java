package com.teixeirah.debugattor.infrastructure.secondary.persistence;

import com.teixeirah.debugattor.domain.execution.Execution;
import com.teixeirah.debugattor.domain.execution.ExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.jooq.generated.Tables.EXECUTIONS;
import static org.jooq.impl.DSL.asterisk;

@Repository
@RequiredArgsConstructor
class ExecutionRepositoryImpl implements ExecutionRepository {

    private final DSLContext context;

    @Override
    public Execution create() {
        return context.insertInto(EXECUTIONS)
                .defaultValues()
                .returningResult(asterisk())
                .fetchOneInto(Execution.class);
    }

    @Override
    public List<Execution> findAll() {
        return context.selectFrom(EXECUTIONS)
                .orderBy(EXECUTIONS.STARTEDAT.desc())
                .fetchInto(Execution.class);
    }

    @Override
    public Optional<Execution> findById(UUID id) {
        return context.select(asterisk())
                .from(EXECUTIONS)
                .where(EXECUTIONS.ID.eq(id))
                .fetchOptionalInto(Execution.class);
    }
}
