package io.gameapis.games.minecraft.implementation;

import io.gameapis.games.minecraft.implementation.data.ProfileData;
import io.gameapis.games.minecraft.implementation.data.UniqueIdData;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class MinecraftRestClient {

    private final static String UNIQUE_ID_FETCH_URL = "https://api.mojang.com/users/profiles/minecraft/{username}";
    private final static String PROFILE_FETCH_URL = "https://sessionserver.mojang.com/session/minecraft/profile/{uniqueId}/?unsigned=false";

    private TcpClient tcpClient = TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
            .doOnConnected(connection -> connection
                    .addHandlerLast(new ReadTimeoutHandler(1000, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(1000, TimeUnit.MILLISECONDS)));

    private WebClient client = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @Async
    public CompletableFuture<UniqueIdData> performUniqueIdRequest(String username) {
        return CompletableFuture.completedFuture(client.get()
                .uri(UNIQUE_ID_FETCH_URL, username)
                .retrieve()
                .bodyToMono(UniqueIdData.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Response is empty!")));
    }

    @Async
    public CompletableFuture<ProfileData> performProfileRequest(UUID uniqueId) {
        return CompletableFuture.completedFuture(client.get()
                .uri(PROFILE_FETCH_URL, uniqueId.toString().replace("-", ""))
                .retrieve()
                .bodyToMono(ProfileData.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Response is empty!")));
    }
}
