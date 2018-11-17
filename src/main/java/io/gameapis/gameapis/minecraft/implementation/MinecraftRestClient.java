package io.gameapis.gameapis.minecraft.implementation;

import io.gameapis.gameapis.minecraft.implementation.data.UniqueIdData;
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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class MinecraftRestClient {

    private final static String UUID_FETCH_URL = "https://api.mojang.com/users/profiles/minecraft/{username}";

    private TcpClient tcpClient = TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1)
            .doOnConnected(connection -> connection
                    .addHandlerLast(new ReadTimeoutHandler(1, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(1, TimeUnit.MILLISECONDS)));

    private WebClient client = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @Async
    public CompletableFuture<UniqueIdData> performUniqueIdRequest(String username) {
        return CompletableFuture.completedFuture(client.get()
                .uri(UUID_FETCH_URL, username)
                .retrieve()
                .bodyToMono(UniqueIdData.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Response is empty!")));
    }
}
