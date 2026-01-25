package com.kodegt.debugattor.infrastructure.secondary.persistence;

import com.kodegt.debugattor.domain.artifact.Artifact;
import com.kodegt.debugattor.domain.artifact.ArtifactRepository;
import com.kodegt.debugattor.domain.execution.Execution;
import com.kodegt.debugattor.domain.execution.ExecutionNotFoundException;
import com.kodegt.debugattor.domain.execution.ExecutionRepository;
import com.kodegt.debugattor.domain.step.Step;
import com.kodegt.debugattor.domain.step.StepNotFoundException;
import com.kodegt.debugattor.domain.step.StepRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Records;
import org.jooq.exception.DataAccessException;
import org.jooq.SortField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.jooq.generated.Tables.*;
import static org.jooq.impl.DSL.*;

@Repository
@RequiredArgsConstructor
class JOOQRepository implements ExecutionRepository, StepRepository, ArtifactRepository {

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
        return findAll(Pageable.ofSize(10));
    }

    @Override
    public List<Execution> findAll(Pageable pageable) {
        return context.select(asterisk(),
                        multiset(select(
                                STEPS.ID,
                                STEPS.NAME,
                                STEPS.STATUS,
                                artifactsMultiset(),
                                STEPS.REGISTERED_AT,
                                STEPS.COMPLETED_AT)
                                .from(STEPS)
                                .where(STEPS.EXECUTION_ID.eq(EXECUTIONS.ID))
                                .orderBy(STEPS.REGISTERED_AT.asc(), STEPS.ID.asc()))
                                .as("steps")
                                .convertFrom(rs -> rs.map(Records.mapping(Step::load)))
                )
                .from(EXECUTIONS)
                .orderBy(getSortFields(pageable.getSort()))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Execution.class);
    }

    @Override
    public List<Execution> findAll(String id, Pageable pageable) {
        var query = context.select(asterisk(),
                        multiset(select(
                                STEPS.ID,
                                STEPS.NAME,
                                STEPS.STATUS,
                                artifactsMultiset(),
                                STEPS.REGISTERED_AT,
                                STEPS.COMPLETED_AT)
                                .from(STEPS)
                                .where(STEPS.EXECUTION_ID.eq(EXECUTIONS.ID))
                                .orderBy(STEPS.REGISTERED_AT.asc(), STEPS.ID.asc()))
                                .as("steps")
                                .convertFrom(rs -> rs.map(Records.mapping(Step::load)))
                )
                .from(EXECUTIONS);

        if (Strings.isNotBlank(id)) {
            query.where(EXECUTIONS.ID.cast(String.class).likeIgnoreCase("%" + id + "%"));
        }

        return query.orderBy(getSortFields(pageable.getSort()))
                .limit(pageable.getPageSize())
                .offset(pageable.getOffset())
                .fetchInto(Execution.class);
    }

    private Collection<SortField<?>> getSortFields(Sort sort) {
        if (sort.isUnsorted()) {
            return List.of(EXECUTIONS.STARTED_AT.desc());
        }

        return sort.stream()
                .map(order -> {
                    Field<?> field = switch (order.getProperty()) {
                        case "status" -> EXECUTIONS.STATUS;
                        case "finishedAt" -> EXECUTIONS.FINISHED_AT;
                        default -> EXECUTIONS.STARTED_AT;
                    };
                    return order.isAscending() ? field.asc() : field.desc();
                })
                .toList();
    }

    @Override
    public Optional<Execution> findById(UUID id) {
        return context.select(asterisk(),
                        multiset(select(
                                STEPS.ID,
                                STEPS.NAME,
                                STEPS.STATUS,
                                artifactsMultiset(),
                                STEPS.REGISTERED_AT,
                                STEPS.COMPLETED_AT)
                                .from(STEPS)
                                .where(STEPS.EXECUTION_ID.eq(EXECUTIONS.ID))
                                .orderBy(STEPS.REGISTERED_AT.asc(), STEPS.ID.asc()))
                                .as("steps")
                                .convertFrom(rs -> rs.map(Records.mapping(Step::load)))
                )
                .from(EXECUTIONS)
                .where(EXECUTIONS.ID.eq(id))
                .fetchOptionalInto(Execution.class);
    }

    private Field<List<Artifact>> artifactsMultiset() {
        return multiset(
                select(ARTIFACTS.ID, ARTIFACTS.TYPE, ARTIFACTS.DESCRIPTION, ARTIFACTS.CONTENT, ARTIFACTS.LOGGED_AT)
                        .from(ARTIFACTS)
                        .where(ARTIFACTS.STEP_ID.eq(STEPS.ID))
                        .orderBy(ARTIFACTS.LOGGED_AT.asc()))
                .convertFrom(rs -> rs.map(
                        Records.mapping(Artifact::newArtifact)))
                .as("artifacts");
    }

    @Override
    public Step register(UUID executionId, Step step) {
        try {
            var record = context.insertInto(STEPS)
                    .set(STEPS.EXECUTION_ID, executionId)
                    .set(STEPS.NAME, step.name())
                    .set(STEPS.STATUS, step.status().name())
                    .returningResult(asterisk())
                    .fetchOne();
            if (record == null) {
                throw new DataAccessException("Failed to insert Step");
            }
            return Step.load(
                record.get(STEPS.ID),
                record.get(STEPS.NAME),
                record.get(STEPS.STATUS),
                List.of(), // no artifacts at registration
                record.get(STEPS.REGISTERED_AT),
                record.get(STEPS.COMPLETED_AT)
            );
        } catch (DataAccessException dae) {
            if (isForeignKeyViolation(dae)) {
                throw new ExecutionNotFoundException(executionId);
            }
            throw dae;
        }
    }

    @Override
    public void setCompleted(UUID stepId) {
        context.update(STEPS)
                .set(STEPS.STATUS, Step.Status.COMPLETED.name())
                .set(STEPS.COMPLETED_AT, currentOffsetDateTime())
                .where(STEPS.ID.eq(stepId))
                .execute();
    }

    @Override
    public void setFailed(UUID stepId) {
        context.update(STEPS)
                .set(STEPS.STATUS, Step.Status.FAILED.name())
                .set(STEPS.COMPLETED_AT, currentOffsetDateTime())
                .where(STEPS.ID.eq(stepId))
                .execute();
    }

    @Override
    public Artifact log(UUID stepId, Artifact.Type type, String description, String content) {
        try {
            return context.insertInto(ARTIFACTS)
                    .set(ARTIFACTS.STEP_ID, stepId)
                    .set(ARTIFACTS.TYPE, type.name())
                    .set(ARTIFACTS.DESCRIPTION, description)
                    .set(ARTIFACTS.CONTENT, content)
                    .returningResult(asterisk())
                    .fetchOneInto(Artifact.class);
        } catch (DataAccessException dae) {
            if (isForeignKeyViolation(dae)) {
                throw new StepNotFoundException(stepId);
            }
            throw dae;
        }
    }

    @Override
    public Artifact createWithoutUrl(UUID stepId, Artifact.Type type, String description) {
        var record = context.insertInto(ARTIFACTS)
                .set(ARTIFACTS.STEP_ID, stepId)
                .set(ARTIFACTS.TYPE, type.name())
                .set(ARTIFACTS.DESCRIPTION, description)
                .set(ARTIFACTS.CONTENT, Strings.EMPTY)
                .returningResult(ARTIFACTS.ID, ARTIFACTS.TYPE, ARTIFACTS.DESCRIPTION, ARTIFACTS.CONTENT, ARTIFACTS.LOGGED_AT)
                .fetchOne();
        return Artifact.newArtifact(
                record.get(ARTIFACTS.ID),
                record.get(ARTIFACTS.TYPE),
                record.get(ARTIFACTS.DESCRIPTION),
                record.get(ARTIFACTS.CONTENT),
                record.get(ARTIFACTS.LOGGED_AT)
        );
    }

    @Override
    public void updateContent(UUID artifactId, String url) {
        context.update(ARTIFACTS)
                .set(ARTIFACTS.CONTENT, url)
                .where(ARTIFACTS.ID.eq(artifactId))
                .execute();
    }

    @Override
    public boolean deleteById(UUID executionId) {
        var steps = context.select(STEPS.ID)
                .from(STEPS)
                .where(STEPS.EXECUTION_ID.eq(executionId))
                .fetch(STEPS.ID);

        if (steps.isEmpty()) {
            var exists = context.fetchExists(EXECUTIONS, EXECUTIONS.ID.eq(executionId));
            if (!exists) return false;
        }

        context.deleteFrom(ARTIFACTS)
                .where(ARTIFACTS.STEP_ID.in(steps))
                .execute();
        context.deleteFrom(STEPS)
                .where(STEPS.EXECUTION_ID.eq(executionId))
                .execute();
        int deleted = context.deleteFrom(EXECUTIONS)
                .where(EXECUTIONS.ID.eq(executionId))
                .execute();
        return deleted > 0;
    }

    @Override
    public void complete(UUID id) {
        context.update(EXECUTIONS)
                .set(EXECUTIONS.STATUS, Execution.Status.COMPLETED.name())
                .set(EXECUTIONS.FINISHED_AT, currentOffsetDateTime())
                .where(EXECUTIONS.ID.eq(id))
                .execute();
    }

    @Override
    public void fail(UUID id) {
        context.update(EXECUTIONS)
                .set(EXECUTIONS.STATUS, Execution.Status.FAILED.name())
                .set(EXECUTIONS.FINISHED_AT, currentOffsetDateTime())
                .where(EXECUTIONS.ID.eq(id))
                .execute();
    }


    @Override
    public List<String> findImagesByExecutionId(UUID executionId) {
        return context.select(ARTIFACTS.CONTENT)
                .from(ARTIFACTS)
                .join(STEPS).on(ARTIFACTS.STEP_ID.eq(STEPS.ID))
                .where(STEPS.EXECUTION_ID.eq(executionId).and(ARTIFACTS.TYPE.eq("IMAGE")))
                .fetch(r -> r.get(ARTIFACTS.CONTENT));
    }

    private static boolean isForeignKeyViolation(Throwable t) {
        // SQLState 23503 is foreign_key_violation in Postgres
        Throwable cur = t;
        while (cur != null) {
            String msg = cur.getMessage();
            if (msg != null && msg.contains("violates foreign key constraint")) {
                return true;
            }
            try {
                // Try to reflectively check SQLState if present (e.g., PSQLException)
                var method = cur.getClass().getMethod("getSQLState");
                Object state = method.invoke(cur);
                if (state != null && "23503".equals(state.toString())) {
                    return true;
                }
            } catch (Exception ignore) {
                // ignore, continue scanning cause chain
            }
            cur = cur.getCause();
        }
        return false;
    }
}
