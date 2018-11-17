package io.gameapis.gameapis.minecraft.api;

import io.gameapis.gameapis.minecraft.api.response.UniqueIDResponse;
import io.gameapis.gameapis.minecraft.implementation.MinecraftAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mc")
public class MinecraftController {

    @Autowired
    private MinecraftAPIService apiService;

    @RequestMapping(value = "/player/uuid/{username}", method = RequestMethod.GET)
    public UniqueIDResponse getUniqueID(@PathVariable String username) {
        if (username.length() > 16) {
            throw new IllegalArgumentException("Only usernames are supported.");
        }
        return UniqueIDResponse.builder().build();
    }
}
