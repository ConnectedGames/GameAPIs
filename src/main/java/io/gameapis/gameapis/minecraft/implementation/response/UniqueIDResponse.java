package io.gameapis.gameapis.minecraft.implementation.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UniqueIDResponse {

    private String id;
    private String name;
}
