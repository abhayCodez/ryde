package com.ryde.rideservice.client;

import com.ryde.rideservice.dto.NearbyDriverResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class RealtimeClient {

    private final WebClient webClient;

    public RealtimeClient(WebClient.Builder builder, @Value("${realtime.service.url}") String baseUrl,
                          @Value("${internal.key}") String internalKey) {
        this.webClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("X-INTERNAL-KEY", internalKey)
                .build();
    }

    public List<NearbyDriverResponse> getNearbyDrivers(double lat, double lng) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/drivers/nearby")
                        .queryParam("lat", lat)
                        .queryParam("lng", lng)
                        .queryParam("radiusKm", 5)
                        .build())
                .retrieve()
                .bodyToFlux(NearbyDriverResponse.class)
                .collectList()
                .block();
    }
}