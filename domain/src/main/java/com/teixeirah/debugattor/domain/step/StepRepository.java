package com.teixeirah.debugattor.domain.step;

import java.util.UUID;

public interface StepRepository {
    void register(UUID executionId, Step step);
}
