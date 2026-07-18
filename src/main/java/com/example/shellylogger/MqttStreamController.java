package com.example.shellylogger;

import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
public class MqttStreamController {

    // A list to keep track of all open browser tabs listening to us
    private final List<SseEmitter> emitters = new ArrayList<>();

    // 1. The URL endpoint the browser connects to
    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamData() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE); // Infinite timeout
        this.emitters.add(emitter);

        // Clean up when the browser tab closes
        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));

        return emitter;
    }

    // 2. Catch the internal shout from MqttLogger and push it out to the browsers
    @EventListener
    public void handleMqttEvent(String payload) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        for (SseEmitter emitter : emitters) {
            try {
                // Send data to the web browser tab
                emitter.send(SseEmitter.event().data(payload));
            } catch (IOException e) {
                deadEmitters.add(emitter); // Mark broken connections for deletion
            }
        }
        emitters.removeAll(deadEmitters);
    }
}