package io.gameapis.gameapis.minecraft.implementation.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class UniqueIdData {

    private String id;
    private String name;
}
