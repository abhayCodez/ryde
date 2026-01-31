package com.ryde.rideservice.client;

import com.ryde.rideservice.dto.NotificationEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NotificationClient {

    private final WebClient webClient;

    public NotificationClient(WebClient.Builder builder,
                              @Value("${notification.service.url}") String baseUrl,
                              @Value("${internal.key}") String internalKey) {

        this.webClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("X-INTERNAL-KEY", internalKey)
                .build();
    }

    public void notifyDriver(NotificationEvent event){
        webClient.post()
                .uri("/internal/notify/driver")
                .bodyValue(event)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    public void notifyRider(NotificationEvent event){
        webClient.post()
                .uri("/internal/notify/rider")
                .bodyValue(event)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}