package io.gameapis.gameapis.minecraft.api;

import io.gameapis.gameapis.minecraft.api.response.UniqueIdResponse;
import io.gameapis.gameapis.minecraft.implementation.MinecraftService;
import io.gameapis.gameapis.utility.UniqueIdUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

@RestController
@RequestMapping("/api/mc")
public class MinecraftController {

    @Autowired
    private MinecraftService minecraftService;

    @RequestMapping(value = "/player/uuid/{username}", method = RequestMethod.GET)
    public DeferredResult<ResponseEntity<?>> getUniqueID(@PathVariable String username) {
        final DeferredResult<ResponseEntity<?>> result = new DeferredResult<>(5000L);

        if (username.length() > 16) {
            result.setResult(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only usernames are supported."));
            return result;
        }

        result.onTimeout(() -> result.setErrorResult(ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                .body("Request timeout occurred.")));

        minecraftService.fetchUniqueId(username).thenAccept(uniqueIdData ->
                result.setResult(ResponseEntity.ok(UniqueIdResponse.builder()
                        .name(uniqueIdData.getName())
                        .id(uniqueIdData.getId())
                        .uuid_formatted(UniqueIdUtilities.parseDashLessUniqueId(uniqueIdData.getId()).toString())
                        .build())));

        return result;
    }
}
