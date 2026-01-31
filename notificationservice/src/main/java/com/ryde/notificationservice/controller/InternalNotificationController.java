package com.ryde.notificationservice.controller;

import com.ryde.notificationservice.dto.NotificationEvent;
import com.ryde.notificationservice.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/internal/notify")
public class InternalNotificationController {

    @Value("${internal.key}")
    private String INTERNAL_KEY;
    private final NotificationService streamService;

    public InternalNotificationController(NotificationService streamService) {
        this.streamService = streamService;
    }

    @PostMapping("/driver")
    public Mono<Void> notifyDriver(@RequestBody NotificationEvent event,
                                   @RequestHeader("X-INTERNAL-KEY") String key) {
        if (!INTERNAL_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        streamService.notifyDriver(event);
        return Mono.empty();
    }

    @PostMapping("/rider")
    public Mono<Void> notifyRider(@RequestBody NotificationEvent event,
                                  @RequestHeader("X-INTERNAL-KEY") String key) {
        if (!INTERNAL_KEY.equals(key)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        streamService.notifyRider(event);
        return Mono.empty();
    }
}