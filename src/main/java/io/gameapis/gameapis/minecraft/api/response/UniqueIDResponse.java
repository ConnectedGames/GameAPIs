package io.gameapis.gameapis.minecraft.api.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UniqueIDResponse {

    private String name;
    private String id;
    private String uuid_formatted;
}
