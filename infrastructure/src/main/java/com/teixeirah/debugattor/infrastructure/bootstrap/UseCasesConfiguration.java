package com.teixeirah.debugattor.infrastructure.bootstrap;

import com.teixeirah.debugattor.application.output.BucketStorageOutputPort;
import com.teixeirah.debugattor.application.usecases.*;
import com.teixeirah.debugattor.domain.artifact.ArtifactRepository;
import com.teixeirah.debugattor.domain.events.EventPublisher;
import com.teixeirah.debugattor.domain.execution.ExecutionRepository;
import com.teixeirah.debugattor.domain.step.StepRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfiguration {

    @Bean
    public StartExecutionUseCase startExecutionUseCase(ExecutionRepository repository) {
        return new StartExecutionUseCase(repository);
    }

    @Bean
    public FetchExecutionsUseCase fetchExecutionsUseCase(ExecutionRepository repository) {
        return new FetchExecutionsUseCase(repository);
    }

    @Bean
    public GetExecutionByIdUseCase getExecutionByIdUseCase(ExecutionRepository repository) {
        return new GetExecutionByIdUseCase(repository);
    }

    @Bean
    public RegisterStepUseCase registerStepUseCase(StepRepository repository,  EventPublisher eventPublisher) {
        return new RegisterStepUseCase(repository, eventPublisher);
    }

    @Bean
    public LogArtifactUseCase logArtifactUseCase(ArtifactRepository repository, BucketStorageOutputPort bucketStorage,  EventPublisher eventPublisher) {
        return new LogArtifactUseCase(repository, bucketStorage, eventPublisher);
    }

    @Bean
    public DeleteExecutionUseCase deleteExecutionUseCase(ExecutionRepository repository, ArtifactRepository artifactRepository, BucketStorageOutputPort bucketStorage) {
        return new DeleteExecutionUseCase(repository, artifactRepository, bucketStorage);
    }

    @Bean
    public CompleteStepUseCase completeStepUseCase(StepRepository repository) {
        return new CompleteStepUseCase(repository);
    }

}
