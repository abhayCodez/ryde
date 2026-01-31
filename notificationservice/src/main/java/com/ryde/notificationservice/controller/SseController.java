package com.ryde.notificationservice.controller;

import com.ryde.notificationservice.dto.NotificationEvent;
import com.ryde.notificationservice.filter.JwtAuthenticationToken;
import com.ryde.notificationservice.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/connect")
public class SseController {

    private final NotificationService streamService;

    public SseController(NotificationService streamService) {
        this.streamService = streamService;
    }

    @GetMapping(value = "/driver/{driverId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationEvent>> connectDriver(@PathVariable Long driverId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (JwtAuthenticationToken) ctx.getAuthentication())
                .flatMapMany(auth -> {
                    if (!auth.getUserId().equals(driverId)
                            || !auth.getAuthorities().contains(
                            new SimpleGrantedAuthority("ROLE_DRIVER"))) {
                        return Flux.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
                    }
                    return streamService.connectDriver(driverId);
                });
    }

    @GetMapping(value = "/rider/{riderId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<NotificationEvent>> connectRider(@PathVariable Long riderId) {
        return ReactiveSecurityContextHolder.getContext()
                .map(ctx -> (JwtAuthenticationToken) ctx.getAuthentication())
                .flatMapMany(auth -> {
                    if (!auth.getUserId().equals(riderId)
                            || !auth.getAuthorities().contains(
                            new SimpleGrantedAuthority("ROLE_RIDER"))) {
                        return Flux.error(new ResponseStatusException(HttpStatus.FORBIDDEN));
                    }
                    return streamService.connectRider(riderId);
                });
    }
}