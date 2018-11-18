package io.gameapis.games.minecraft.api.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UniqueIdResponse {

    private String name;
    private String id;
    private String uuid_formatted;
}
