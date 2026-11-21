package com.ryde.notificationservice;

import com.ryde.notificationservice.dto.NotificationEvent;
import com.ryde.notificationservice.service.NotificationService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Boots the notification service (it needs no database) and checks its security rules
 * with access tokens shaped like the ones authservice issues.
 */
@SpringBootTest(properties = {
        "application.security.jwt.secret-key=" + NotificationSecurityTest.SECRET,
        "internal.key=" + NotificationSecurityTest.INTERNAL_KEY
})
@AutoConfigureWebTestClient
class NotificationSecurityTest {

    /** Base64 of a 280-bit key used only by this test. */
    static final String SECRET = "cnlkZS1ub3RpZmljYXRpb24tdGVzdC1vbmx5LWtleS0yNTY=";
    static final String INTERNAL_KEY = "test-internal-key";

    private static final String EVENT_JSON =
            "{\"userId\":7,\"type\":\"REQUESTED\",\"totalFare\":86,\"distanceInKm\":2.4,\"eta\":5}";

    @Autowired
    private WebTestClient client;

    @Autowired
    private NotificationService notifications;

    @Test
    void internalNotifyAcceptsTheInternalKey() {
        client.post().uri("/internal/notify/driver")
                .header("X-INTERNAL-KEY", INTERNAL_KEY)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(EVENT_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void internalNotifyRejectsAWrongKey() {
        client.post().uri("/internal/notify/driver")
                .header("X-INTERNAL-KEY", "wrong-key")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(EVENT_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void streamsNeedAToken() {
        client.get().uri("/connect/driver/7")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void aRiderCannotOpenADriverStream() {
        client.get().uri("/connect/driver/7")
                .header(HttpHeaders.AUTHORIZATION, bearer(7, "RIDER"))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void aDriverCannotOpenAnotherDriversStream() {
        client.get().uri("/connect/driver/7")
                .header(HttpHeaders.AUTHORIZATION, bearer(9, "DRIVER"))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void aDriverReceivesRideRequestsOnTheirOwnStream() {
        // Keep publishing until the stream is subscribed; events sent before that are dropped.
        Disposable publisher = Flux.interval(Duration.ofMillis(200))
                .take(25)
                .subscribe(i -> notifications.notifyDriver(
                        new NotificationEvent(7L, "REQUESTED", 86, 2.4, 5)));
        try {
            String firstEvent = client.get().uri("/connect/driver/7")
                    .header(HttpHeaders.AUTHORIZATION, bearer(7, "DRIVER"))
                    .accept(MediaType.TEXT_EVENT_STREAM)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(String.class)
                    .getResponseBody()
                    .blockFirst(Duration.ofSeconds(5));

            assertThat(firstEvent).contains("\"userId\":7", "\"type\":\"REQUESTED\"", "\"eta\":5");
        } finally {
            publisher.dispose();
        }
    }

    private static String bearer(long userId, String role) {
        Date now = new Date();
        String token = Jwts.builder()
                .claims(Map.of("userId", userId, "roles", List.of(role), "ver", 0))
                .subject("user" + userId + "@example.com")
                .issuedAt(now)
                .expiration(new Date(now.getTime() + 15 * 60 * 1000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .compact();
        return "Bearer " + token;
    }
}
