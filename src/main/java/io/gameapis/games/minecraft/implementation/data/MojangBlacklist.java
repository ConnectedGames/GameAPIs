package io.gameapis.games.minecraft.implementation.data;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

@Data
public class MojangBlacklist {
    private List<String> hashes;

    public MojangBlacklist(List<String> hashes) {
        this.hashes = hashes;
    }

    public MojangBlacklist(String hashes) {
        String[] split = StringUtils.split(hashes, "\n");

        if (split != null) {
            this.hashes = Arrays.asList(split);
        } else {
            throw new RuntimeException("Hashes are empty");
        }
    }
}
