package com.teixeirah.debugattor.infrastructure.bootstrap;

import com.teixeirah.debugattor.application.usecases.*;
import com.teixeirah.debugattor.domain.artifact.ArtifactRepository;
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
    public RegisterStepUseCase registerStepUseCase(StepRepository repository) {
        return new RegisterStepUseCase(repository);
    }

    @Bean
    public LogArtifactUseCase logArtifactUseCase(ArtifactRepository repository) {
        return new LogArtifactUseCase(repository);
    }

}
