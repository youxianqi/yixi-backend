package youxianqi.yixi.consts;

import java.util.HashMap;
import java.util.Map;

public enum MediaType {
    VIDEO(1), AUDIO(2), IMAGE(3),TEXT(4),EXERCISE_ITEM(5),PARENT(6);

    private int value;
    private static Map map = new HashMap<>();

    private MediaType(int value) {
        this.value = value;
    }

    static {
        for (MediaType type : MediaType.values()) {
            map.put(type.value, type);
        }
    }

    public static MediaType valueOf(int type) {
        return (MediaType) map.get(type);
    }
    public int getValue() {
        return value;
    }
}
