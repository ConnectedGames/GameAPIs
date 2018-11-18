package io.gameapis.games.minecraft.api;

import io.gameapis.api.response.ErrorResponse;
import io.gameapis.games.minecraft.api.response.ProfileResponse;
import io.gameapis.games.minecraft.api.response.UniqueIdResponse;
import io.gameapis.games.minecraft.implementation.MinecraftService;
import io.gameapis.utility.UniqueIdUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/api/mc")
public class MinecraftController {

    private MinecraftService minecraftService;

    @Autowired
    public MinecraftController(MinecraftService minecraftService) {
        this.minecraftService = minecraftService;
    }

    @RequestMapping(value = "/player/uuid/{username}", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<?>> getUniqueId(@PathVariable String username) {
        final DeferredResult<ResponseEntity<?>> result = new DeferredResult<>(5000L);

        if (username.length() > 16) {
            result.setResult(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.error("Only usernames are supported.")));
            return result;
        }

        result.onTimeout(() -> result.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(ErrorResponse.error("Request timeout occurred."))));

        minecraftService.fetchUniqueId(username).thenAccept(uniqueIdData ->
                result.setResult(ResponseEntity.ok(UniqueIdResponse.builder()
                        .name(uniqueIdData.getName())
                        .id(uniqueIdData.getId())
                        .uuid_formatted(UniqueIdUtilities.parseDashLessUniqueId(uniqueIdData.getId()).toString())
                        .build())));

        return result;
    }

    @RequestMapping(value = "/player/profile/{uniqueId}", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<?>> getProfile(@PathVariable String uniqueId) {
        final DeferredResult<ResponseEntity<?>> result = new DeferredResult<>(5000L);

        if (uniqueId.length() != 32) {
            result.setResult(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.error("Invalid UUID specified.")));
            return result;
        }

        UUID parsedUniqueId;
        try {
            parsedUniqueId = UniqueIdUtilities.parseDashLessUniqueId(uniqueId);
        } catch (NumberFormatException e) {
            result.setResult(ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ErrorResponse.error("Invalid UUID specified.")));
            return result;
        }

        result.onTimeout(() -> result.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body(ErrorResponse.error("Request timeout occurred."))));

        minecraftService.fetchProfile(parsedUniqueId).thenAccept(profileData ->
                result.setResult(ResponseEntity.ok(ProfileResponse.builder()
                        .name(profileData.getName())
                        .id(profileData.getId())
                        .uuid_formatted(UniqueIdUtilities.parseDashLessUniqueId(profileData.getId()).toString())
                        .properties(profileData.getProperties())
                        .properties_decoded(new ArrayList<>())
                        .expiresAt(Instant.now().getEpochSecond())
                        .expiresAtHR(Instant.now())
                        .build())));

        return result;
    }
}
