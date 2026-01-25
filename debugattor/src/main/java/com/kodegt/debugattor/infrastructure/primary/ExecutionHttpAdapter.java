package com.kodegt.debugattor.infrastructure.primary;

import com.kodegt.debugattor.application.input.*;
import com.kodegt.debugattor.domain.artifact.Artifact;
import com.kodegt.debugattor.domain.artifact.FileMetadata;
import com.kodegt.debugattor.domain.execution.ExecutionNotFoundException;
import com.kodegt.debugattor.infrastructure.primary.dto.ArtifactResponse;
import com.kodegt.debugattor.infrastructure.primary.dto.ExecutionResponse;
import com.kodegt.debugattor.infrastructure.primary.dto.StepResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/executions")
class ExecutionHttpAdapter {

    private final StartExecutionUseCase startExecutionUseCase;
    private final FetchExecutionsUseCase fetchExecutionsUseCase;
    private final GetExecutionByIdUseCase getExecutionByIdUseCase;
    private final RegisterStepUseCase registerStepUseCase;
    private final LogArtifactUseCase logArtifactUseCase;
    private final DeleteExecutionUseCase deleteExecutionUseCase;
    private final CompleteStepUseCase completeStepUseCase;
    private final FailStepUseCase failStepUseCase;
    private final CompleteExecutionUseCase completeExecutionUseCase;
    private final FailExecutionUseCase failExecutionUseCase;

    @PostMapping
    ResponseEntity<ExecutionResponse> startExecution() {
        return ResponseEntity.ok(ExecutionResponse.from(startExecutionUseCase.execute()));
    }

    @GetMapping
    ResponseEntity<List<ExecutionResponse>> fetchExecutions() {
        List<ExecutionResponse> executions = fetchExecutionsUseCase.execute()
                .stream()
                .map(ExecutionResponse::from)
                .toList();
        return ResponseEntity.ok(executions);
    }

    @GetMapping("/{executionId}")
    ResponseEntity<ExecutionResponse> getExecutionById(@PathVariable UUID executionId) {
        return getExecutionByIdUseCase.execute(executionId)
                .map(ExecutionResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{executionId}/steps")
    ResponseEntity<StepResponse> registerStep(@PathVariable UUID executionId, @RequestBody RegisterStepDto dto) {
        final var step = registerStepUseCase.execute(executionId, dto.name());
        return ResponseEntity.ok(StepResponse.from(step));
    }

    public record RegisterStepDto(String name) {
    }

    @PostMapping("/{executionId}/steps/{stepId}/artifacts")
    ResponseEntity<ArtifactResponse> logArtifact(@PathVariable UUID executionId, @PathVariable UUID stepId, @RequestBody LogArtifact dto) {
        final var artifact = logArtifactUseCase.log(stepId, Artifact.Type.valueOf(dto.type()), dto.description(), dto.content());
        return ResponseEntity.ok(ArtifactResponse.from(artifact));
    }

    @PostMapping("/{executionId}/steps/{stepId}/artifacts/upload")
    public ResponseEntity<ArtifactResponse> uploadFile(@PathVariable UUID executionId,
                                               @PathVariable UUID stepId,
                                               @ModelAttribute LogArtifact dto) throws IOException {
        final var file = dto.file();
        final var metadata = new FileMetadata(file.getOriginalFilename(), file.getContentType(), file.getSize());

        final var artifact = logArtifactUseCase.logFile(stepId,
                Artifact.Type.valueOf(dto.type()),
                dto.description(),
                file.getInputStream(),
                metadata);

        return ResponseEntity.ok(ArtifactResponse.from(artifact));
    }

    @DeleteMapping("/{executionId}")
    ResponseEntity<Void> deleteExecution(@PathVariable UUID executionId) {
        try {
            deleteExecutionUseCase.delete(executionId);
            return ResponseEntity.noContent().build();
        } catch (ExecutionNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{executionId}/steps/{stepId}/complete")
    ResponseEntity<Void> completeStep(@PathVariable UUID executionId, @PathVariable UUID stepId) {
        completeStepUseCase.execute(stepId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{executionId}/steps/{stepId}/fail")
    ResponseEntity<Void> failStep(@PathVariable UUID executionId, @PathVariable UUID stepId) {
        failStepUseCase.execute(stepId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{executionId}/complete")
    ResponseEntity<Void> completeExecution(@PathVariable UUID executionId) {
        completeExecutionUseCase.execute(executionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{executionId}/fail")
    ResponseEntity<Void> failExecution(@PathVariable UUID executionId) {
        failExecutionUseCase.execute(executionId);
        return ResponseEntity.noContent().build();
    }

    public record LogArtifact(String type, String description, String content, MultipartFile file) {
    }


}
