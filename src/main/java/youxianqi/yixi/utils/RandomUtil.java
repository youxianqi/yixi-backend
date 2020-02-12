package youxianqi.yixi.utils;

import java.util.Random;
import java.util.UUID;

public class RandomUtil {
    private static final char[] DIGITS = "0123456789".toCharArray();

    public static String randomUUID() {
        UUID uuid = UUID.randomUUID();
        return String.format("%016x", uuid.getMostSignificantBits()) + String.format("%016x", uuid.getLeastSignificantBits());
    }

    public static String randomInteger(int length) {
        Random rand = new Random();
        StringBuilder res = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            res.append(DIGITS[rand.nextInt(DIGITS.length)]);
        }
        return res.toString();
    }
}
