package com.teixeirah.debugattor.application.input;

import com.teixeirah.debugattor.domain.execution.Execution;

import java.util.List;

public interface FetchExecutionsUseCase {

    List<Execution> execute();
}
