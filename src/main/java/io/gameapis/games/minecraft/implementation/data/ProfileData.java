package io.gameapis.games.minecraft.implementation.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class ProfileData {

    private String id;
    private String name;
    private List<Map<String, String>> properties;
}
