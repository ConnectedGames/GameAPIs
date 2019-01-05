package io.gameapis.games.minecraft.implementation;

import io.gameapis.games.minecraft.implementation.data.BlacklistData;
import io.gameapis.games.minecraft.implementation.data.ProfileData;
import io.gameapis.games.minecraft.implementation.data.UniqueIdData;
import io.gameapis.utility.SHA1Utilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
public class MinecraftService {

    private final MinecraftRestClient client;
    private final MinecrafBlacklistClient minecrafBlacklistClient;

    @Autowired
    public MinecraftService(MinecraftRestClient minecraftRestClient, MinecrafBlacklistClient minecrafBlacklistClient) {
        this.client = minecraftRestClient;
        this.minecrafBlacklistClient = minecrafBlacklistClient;
    }

    @Cacheable("minecraft.uniqueId")
    public CompletableFuture<UniqueIdData> fetchUniqueId(String name) {
        return client.performUniqueIdRequest(name);
    }

    @Cacheable("minecraft.profile")
    public CompletableFuture<ProfileData> fetchProfile(UUID uniqueId) {
        return client.performProfileRequest(uniqueId);
    }

    @Cacheable("minecraft.blacklisted")
    public CompletableFuture<BlacklistData> fetchBlacklisted(String domain) {
        return minecrafBlacklistClient.performBlacklistRequest().handle((mojangBlacklist, throwable) -> {
            BlacklistData blacklistData = new BlacklistData(domain);

            blacklistData.getBlackListMap().forEach((s, aBoolean) -> {
                String stringToSHA1;
                try {
                    stringToSHA1 = SHA1Utilities.stringToSHA1(s);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();

                    return;
                }

                System.out.println(mojangBlacklist.getHashes().size());
                if (mojangBlacklist.getHashes().contains(stringToSHA1)) {
                    blacklistData.getBlackListMap().put(s, true);
                }
            });

            return blacklistData;
        });
    }
}
