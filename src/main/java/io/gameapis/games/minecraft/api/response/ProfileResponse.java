package io.gameapis.games.minecraft.api.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class ProfileResponse {

    private String id;
    private String uuid_formatted;
    private String name;
    private List<Map<String, String>> properties;
    private List<Map<String, String>> properties_decoded;
    private Long expiresAt;
    private Instant expiresAtHR;
}
