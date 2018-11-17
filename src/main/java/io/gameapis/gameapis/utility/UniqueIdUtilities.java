package io.gameapis.gameapis.utility;

import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.util.UUID;

@UtilityClass
public class UniqueIdUtilities {

    public UUID parseDashLessUniqueId(String string) {
        BigInteger msb = new BigInteger(string.substring(0, 16), 16);
        BigInteger lsb = new BigInteger(string.substring(16, 32), 16);
        return new UUID(msb.longValue(), lsb.longValue());
    }
}
