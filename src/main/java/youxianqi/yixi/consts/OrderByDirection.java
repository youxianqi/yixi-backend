package youxianqi.yixi.consts;

import java.util.HashMap;
import java.util.Map;

public enum OrderByDirection {
    ASC(1), DESC(2);

    private int value;
    private static Map map = new HashMap<>();

    private OrderByDirection(int value) {
        this.value = value;
    }

    static {
        for (OrderByDirection type : OrderByDirection.values()) {
            map.put(type.value, type);
        }
    }

    public static OrderByDirection valueOf(int type) {
        return (OrderByDirection) map.get(type);
    }
    public int getValue() {
        return value;
    }
}
