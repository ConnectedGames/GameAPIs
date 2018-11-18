package io.gameapis.games.minecraft.implementation;

import io.gameapis.games.minecraft.implementation.data.ProfileData;
import io.gameapis.games.minecraft.implementation.data.UniqueIdData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class MinecraftService {

    private MinecraftRestClient client;

    @Autowired
    public MinecraftService(MinecraftRestClient client) {
        this.client = client;
    }

    @Cacheable("minecraft.uniqueId")
    public CompletableFuture<UniqueIdData> fetchUniqueId(String name) {
        return client.performUniqueIdRequest(name);
    }

    @Cacheable("minecraft.profile")
    public CompletableFuture<ProfileData> fetchProfile(UUID uniqueId) {
        return client.performProfileRequest(uniqueId);
    }
}
