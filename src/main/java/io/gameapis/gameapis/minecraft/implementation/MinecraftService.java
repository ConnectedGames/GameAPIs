package io.gameapis.gameapis.minecraft.implementation;

import io.gameapis.gameapis.minecraft.implementation.data.ProfileData;
import io.gameapis.gameapis.minecraft.implementation.data.UniqueIdData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class MinecraftService {

    @Autowired
    private MinecraftRestClient client;

    @Cacheable("minecraft.uniqueId")
    public CompletableFuture<UniqueIdData> fetchUniqueId(String name) {
        return client.performUniqueIdRequest(name);
    }

    @Cacheable("minecraft.profile")
    public CompletableFuture<ProfileData> fetchProfile(UUID uniqueId) {
        return client.performProfileRequest(uniqueId);
    }
}
