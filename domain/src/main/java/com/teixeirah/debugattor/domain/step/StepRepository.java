package com.teixeirah.debugattor.domain.step;

import java.util.UUID;

public interface StepRepository {
    Step register(UUID executionId);
}
