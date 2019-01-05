package io.gameapis.games.minecraft.implementation;

import io.gameapis.games.minecraft.implementation.data.MojangBlacklist;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.cache.annotation.Cacheable;
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
public class MinecrafBlacklistClient {

    private static final String BLACKLIST_URL = "https://sessionserver.mojang.com/blockedservers";

    private TcpClient tcpClient = TcpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
            .doOnConnected(connection -> connection
                    .addHandlerLast(new ReadTimeoutHandler(1000, TimeUnit.MILLISECONDS))
                    .addHandlerLast(new WriteTimeoutHandler(1000, TimeUnit.MILLISECONDS)));

    private WebClient client = WebClient.builder()
            .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @Cacheable("minecraft.blacklist")
    @Async
    public CompletableFuture<MojangBlacklist> performBlacklistRequest() {
        String s = client.get()
                .uri(BLACKLIST_URL)
                .retrieve()
                .bodyToMono(String.class)
                .blockOptional()
                .orElseThrow(() -> new RuntimeException("Response is empty!"));

        MojangBlacklist mojangBlacklist = new MojangBlacklist(s);

        return CompletableFuture.completedFuture(mojangBlacklist);
    }
}
