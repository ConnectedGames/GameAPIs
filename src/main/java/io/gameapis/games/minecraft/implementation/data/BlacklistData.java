package io.gameapis.games.minecraft.implementation.data;

import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.HashMap;

@Data
public class BlacklistData {
    private HashMap<String, Boolean> blackListMap = new HashMap<>();

    public BlacklistData(String domain) {
        blackListMap.put(domain, false);
        blackListMap.put(getBaseDomain(domain), false);
        blackListMap.put(getWildcardDomain(domain), false);
    }

    private String getBaseDomain(String domain) {
        if (StringUtils.countOccurrencesOf(domain, ".") > 1) {
            return domain.substring(domain.indexOf(".") + 1);
        }

        return domain;
    }

    private String getWildcardDomain(String domain) {
        return "*." + getBaseDomain(domain);
    }
}
