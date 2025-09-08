package com.teixeirah.debugattor.infrastructure.secondary.persistence;

import com.teixeirah.debugattor.domain.artifact.Artifact;
import com.teixeirah.debugattor.domain.artifact.ArtifactRepository;
import com.teixeirah.debugattor.domain.execution.Execution;
import com.teixeirah.debugattor.domain.execution.ExecutionRepository;
import com.teixeirah.debugattor.domain.step.Step;
import com.teixeirah.debugattor.domain.step.StepRepository;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Records;
import org.jooq.generated.Tables;
import org.springframework.stereotype.Repository;

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
        Field<List<Artifact>> artifactsField =
                multiset(
                        select(ARTIFACTS.ID, ARTIFACTS.TYPE, ARTIFACTS.CONTENT, ARTIFACTS.LOGGED_AT)
                                .from(ARTIFACTS)
                                .where(ARTIFACTS.STEP_ID.eq(STEPS.ID)))
                        .convertFrom(rs -> rs.map(
                                Records.mapping(Artifact::newArtifact)))
                        .as("artifacts");

        return context.select(asterisk(),
                        multiset(select(
                                STEPS.ID,
                                STEPS.NAME,
                                STEPS.STATUS,
                                artifactsField,
                                STEPS.REGISTERED_AT,
                                STEPS.COMPLETED_AT)
                                .from(STEPS)
                                .where(STEPS.EXECUTION_ID.eq(EXECUTIONS.ID)))
                                .as("steps")
                                .convertFrom(rs -> rs.map(Records.mapping(Step::load)))
                )
                .from(EXECUTIONS)
                .fetchInto(Execution.class);
    }

    @Override
    public Optional<Execution> findById(UUID id) {
        Field<List<Artifact>> artifactsField =
                multiset(
                        select(ARTIFACTS.ID, ARTIFACTS.TYPE, ARTIFACTS.CONTENT, ARTIFACTS.LOGGED_AT)
                                .from(ARTIFACTS)
                                .where(ARTIFACTS.STEP_ID.eq(STEPS.ID)))
                        .convertFrom(rs -> rs.map(
                                Records.mapping(Artifact::newArtifact)))
                        .as("artifacts");


        return context.select(asterisk(),
                        multiset(select(
                                STEPS.ID,
                                STEPS.NAME,
                                STEPS.STATUS,
                                artifactsField,
                                STEPS.REGISTERED_AT,
                                STEPS.COMPLETED_AT)
                                .from(STEPS)
                                .where(STEPS.EXECUTION_ID.eq(EXECUTIONS.ID)))
                                .as("steps")
                                .convertFrom(rs -> rs.map(Records.mapping(Step::load)))
                )
                .from(EXECUTIONS)
                .where(EXECUTIONS.ID.eq(id))
                .fetchOptionalInto(Execution.class);
    }

    @Override
    public void register(UUID executionId, Step step) {
        context.insertInto(STEPS)
                .set(STEPS.EXECUTION_ID, executionId)
                .set(STEPS.NAME, step.name())
                .set(STEPS.STATUS, step.status().name())
                .execute();
    }

    @Override
    public Artifact log(UUID stepId, Artifact.Type type, String content) {
        return context.insertInto(ARTIFACTS)
                .set(ARTIFACTS.STEP_ID, stepId)
                .set(ARTIFACTS.TYPE, type.name())
                .set(ARTIFACTS.CONTENT, content)
                .returningResult(asterisk())
                .fetchOneInto(Artifact.class);
    }
}
