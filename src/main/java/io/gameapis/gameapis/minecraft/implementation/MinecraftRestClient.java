package io.gameapis.gameapis.minecraft.implementation;

import io.gameapis.gameapis.minecraft.implementation.data.UniqueIdData;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;

@Component
public class MinecraftRestClient {

    private final static String UUID_FETCH_URL = "https://api.mojang.com/users/profiles/minecraft/{username}";

    private WebClient client = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @Async
    public CompletableFuture<UniqueIdData> performUniqueIdRequest(String username) {
        return CompletableFuture.completedFuture(client.get()
                .uri(UUID_FETCH_URL, username)
                .exchange()
                .flatMap(response -> response.bodyToMono(UniqueIdData.class))
                .timeout(Duration.of(1000, ChronoUnit.MILLIS))
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Response is empty!")));
    }
}
