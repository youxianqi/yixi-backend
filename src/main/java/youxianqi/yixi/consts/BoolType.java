package youxianqi.yixi.consts;

import java.util.HashMap;
import java.util.Map;

public enum BoolType {
    FALSE((byte)0), TRUE((byte)1);

    private byte value;
    private static Map map = new HashMap<>();

    private BoolType(byte value) {
        this.value = value;
    }

    static {
        for (BoolType type : BoolType.values()) {
            map.put(type.value, type);
        }
    }

    public static BoolType valueOf(byte type) {
        return (BoolType) map.get(type);
    }
    public byte getValue() {
        return value;
    }
}
