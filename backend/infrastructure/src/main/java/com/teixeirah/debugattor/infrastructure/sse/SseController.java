package com.teixeirah.debugattor.infrastructure.sse;

import com.teixeirah.debugattor.domain.events.ArtifactLoggedEvent;
import com.teixeirah.debugattor.domain.events.StepRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/events")
public class SseController {
    private final List<SseEmitter> stepEmitters = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> artifactEmitters = new CopyOnWriteArrayList<>();
    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();

    public SseController() {
        // Heartbeat para manter conexões ativas em proxies e durante dev
        heartbeatScheduler.scheduleAtFixedRate(() -> {
            sendHeartbeat(stepEmitters, "steps");
            sendHeartbeat(artifactEmitters, "artifacts");
        }, 15, 15, TimeUnit.SECONDS);
    }

    private void sendHeartbeat(List<SseEmitter> emitters, String channel) {
        for (SseEmitter emitter : new java.util.ArrayList<>(emitters)) {
            try {
                emitter.send(SseEmitter.event().name("heartbeat").data("hb:" + channel + ":" + Instant.now().toString()));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);
            }
        }
    }

    @GetMapping(value = "/steps", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamSteps() {
        SseEmitter emitter = new SseEmitter(0L);
        stepEmitters.add(emitter);
        emitter.onCompletion(() -> stepEmitters.remove(emitter));
        emitter.onTimeout(() -> stepEmitters.remove(emitter));
        // sinaliza conexão estabelecida
        try { emitter.send(SseEmitter.event().name("sse-connected").data("ok")); } catch (IOException ignored) {}
        return emitter;
    }

    @GetMapping(value = "/artifacts", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamArtifacts() {
        SseEmitter emitter = new SseEmitter(0L);
        artifactEmitters.add(emitter);
        emitter.onCompletion(() -> artifactEmitters.remove(emitter));
        emitter.onTimeout(() -> artifactEmitters.remove(emitter));
        // sinaliza conexão estabelecida
        try { emitter.send(SseEmitter.event().name("sse-connected").data("ok")); } catch (IOException ignored) {}
        return emitter;
    }

    @EventListener
    public void onStepRegistered(StepRegisteredEvent event) {
        for (SseEmitter emitter : new java.util.ArrayList<>(stepEmitters)) {
            try {
                emitter.send(SseEmitter.event().name("step-registered").data(event));
            } catch (IOException e) {
                emitter.completeWithError(e);
                stepEmitters.remove(emitter); // Remove imediatamente ao detectar erro
            }
        }
    }

    @EventListener
    public void onArtifactLogged(ArtifactLoggedEvent event) {
        for (SseEmitter emitter : new java.util.ArrayList<>(artifactEmitters)) {
            try {
                emitter.send(SseEmitter.event().name("artifact-registered").data(event));
            } catch (IOException e) {
                emitter.completeWithError(e);
                artifactEmitters.remove(emitter); // Remove imediatamente ao detectar erro
            }
        }
    }
}
