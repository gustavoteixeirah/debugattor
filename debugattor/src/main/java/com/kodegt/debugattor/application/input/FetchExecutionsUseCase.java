package com.kodegt.debugattor.application.input;

import com.kodegt.debugattor.domain.execution.Execution;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FetchExecutionsUseCase {

    List<Execution> execute();

    List<Execution> fetch(Pageable pageable);

    List<Execution> fetch(String id, Pageable pageable);
}
