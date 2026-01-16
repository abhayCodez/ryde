package com.ryde.profileservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class AuthServiceClient {

    private final WebClient webClient;

    public AuthServiceClient(WebClient.Builder builder,
                             @Value("${auth.service.url}") String baseUrl,
                             @Value("${internal.key}") String internalKey) {

        this.webClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("X-INTERNAL-KEY", internalKey)
                .build();
    }

    public void upgradeToDriver(Long userId) {
        webClient.patch()
                .uri("/internal/users/{id}/upgrade-to-driver", userId)
                .bodyValue(Map.of("role", "DRIVER"))
                .retrieve()
                .onStatus(HttpStatusCode::isError, resp ->
                        resp.bodyToMono(String.class)
                                .flatMap(body -> Mono.error(
                                        new RuntimeException("Auth-service failed: " + body)))
                )
                .toBodilessEntity()
                .block();
    }
}