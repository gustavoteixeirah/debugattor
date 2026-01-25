package com.kodegt.debugattor.domain.step;

import java.util.UUID;

public interface StepRepository {
    Step register(UUID executionId, Step step);
    void setCompleted(UUID stepId);
    void setFailed(UUID stepId);

}
