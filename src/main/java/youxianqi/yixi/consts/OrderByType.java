package youxianqi.yixi.consts;

import java.util.HashMap;
import java.util.Map;

public enum OrderByType {
    DATA_TIME(1), UPDATE_TIME(2),VIEWS(3),LIKES(4),FAVS(5)
    ,TAGS(6),ACCESS_TYPE(7);

    private int value;
    private static Map map = new HashMap<>();

    private OrderByType(int value) {
        this.value = value;
    }

    static {
        for (OrderByType type : OrderByType.values()) {
            map.put(type.value, type);
        }
    }

    public static OrderByType valueOf(int type) {
        return (OrderByType) map.get(type);
    }
    public int getValue() {
        return value;
    }
}
