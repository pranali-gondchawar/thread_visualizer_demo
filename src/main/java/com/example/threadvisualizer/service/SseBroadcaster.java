package com.example.threadvisualizer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseBroadcaster {

    private static final Logger log = LoggerFactory.getLogger(SseBroadcaster.class);
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter registerClient() {
        // 30-minute timeout for interactive sessions
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        emitters.add(emitter);

        emitter.onCompletion(() -> {
            log.info("SSE connection completed");
            emitters.remove(emitter);
        });

        emitter.onTimeout(() -> {
            log.info("SSE connection timed out");
            emitter.complete();
            emitters.remove(emitter);
        });

        emitter.onError(e -> {
            log.warn("SSE connection error: {}", e.getMessage());
            emitters.remove(emitter);
        });

        // Send initial connection event
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to ThreadPool SSE Stream"));
        } catch (IOException e) {
            emitters.remove(emitter);
        }

        return emitter;
    }

    public void broadcast(String eventName, Object data) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }
}

