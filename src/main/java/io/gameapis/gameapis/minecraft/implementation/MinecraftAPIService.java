package io.gameapis.gameapis.minecraft.implementation;

import io.gameapis.gameapis.minecraft.implementation.response.UniqueIDResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Future;

@Service
public class MinecraftAPIService {

    private final static String UUID_FETCH_URL = "https://api.mojang.com/users/profiles/minecraft/{username}";

    private WebClient client = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @Async
    public Future<UniqueIDResponse> performUniqueIdRequest(String username) {
        return new AsyncResult<>(client.get()
                .uri(UUID_FETCH_URL, username)
                .retrieve()
                .bodyToMono(UniqueIDResponse.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Response is empty!")));
    }
}
