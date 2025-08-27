package br.com.studybuddy.debugattor.infrastructure.bootstrap;

import br.com.studybuddy.debugattor.application.usecases.FetchExecutionsUseCase;
import br.com.studybuddy.debugattor.application.usecases.GetExecutionByIdUseCase;
import br.com.studybuddy.debugattor.application.usecases.StartExecutionUseCase;
import br.com.studybuddy.debugattor.domain.execution.ExecutionRepository;
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

}
