package com.teixeirah.debugattor.infrastructure.sse;

import com.teixeirah.debugattor.domain.events.EventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class SpringEventPublisher implements EventPublisher {
    private final ApplicationEventPublisher delegate;

    public SpringEventPublisher(ApplicationEventPublisher delegate) {
        this.delegate = delegate;
    }

    @Override
    public void publish(Object event) {
        delegate.publishEvent(event);
    }
}

