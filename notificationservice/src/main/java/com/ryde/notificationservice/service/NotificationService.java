package com.ryde.notificationservice.service;

import com.ryde.notificationservice.dto.NotificationEvent;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private final Map<Long, Sinks.Many<NotificationEvent>> driverSinks = new ConcurrentHashMap<>();
    private final Map<Long, Sinks.Many<NotificationEvent>> riderSinks = new ConcurrentHashMap<>();

    public Flux<ServerSentEvent<NotificationEvent>> connectDriver(Long driverId) {
        Sinks.Many<NotificationEvent> sink =
                driverSinks.computeIfAbsent(driverId,
                        id -> Sinks.many().multicast().onBackpressureBuffer());

        return sink.asFlux()
                .map(event -> ServerSentEvent.builder(event)
                        .event(event.getType())
                        .build());
    }

    public Flux<ServerSentEvent<NotificationEvent>> connectRider(Long riderId) {
        Sinks.Many<NotificationEvent> sink =
                riderSinks.computeIfAbsent(riderId,
                        id -> Sinks.many().multicast().onBackpressureBuffer());

        return sink.asFlux()
                .map(event -> ServerSentEvent.builder(event)
                        .event(event.getType())
                        .build());
    }

    public void notifyDriver(NotificationEvent event) {
        Sinks.Many<NotificationEvent> sink = driverSinks.get(event.getUserId());
        if (sink != null) {
            sink.tryEmitNext(event);
        }
    }

    public void notifyRider(NotificationEvent event) {
        Sinks.Many<NotificationEvent> sink = riderSinks.get(event.getUserId());
        if (sink != null) {
            sink.tryEmitNext(event);
        }
    }
}