package com.teixeirah.debugattor.infrastructure.primary;

import com.teixeirah.debugattor.domain.execution.ExecutionNotFoundException;
import com.teixeirah.debugattor.domain.step.StepNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(ExecutionNotFoundException.class)
    ResponseEntity<Void> handleExecutionNotFound(ExecutionNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(StepNotFoundException.class)
    ResponseEntity<Void> handleStepNotFoundException(StepNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}

