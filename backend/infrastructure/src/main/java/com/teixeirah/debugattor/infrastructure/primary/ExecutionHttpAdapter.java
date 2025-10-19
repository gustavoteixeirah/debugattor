package com.teixeirah.debugattor.infrastructure.primary;

import com.teixeirah.debugattor.application.input.*;
import com.teixeirah.debugattor.domain.artifact.Artifact;
import com.teixeirah.debugattor.domain.artifact.FileMetadata;
import com.teixeirah.debugattor.domain.execution.ExecutionNotFoundException;
import com.teixeirah.debugattor.infrastructure.primary.dto.ArtifactResponse;
import com.teixeirah.debugattor.infrastructure.primary.dto.ExecutionResponse;
import com.teixeirah.debugattor.infrastructure.primary.dto.StepResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    public record LogArtifact(String type, String description, String content, MultipartFile file) {
    }


}
