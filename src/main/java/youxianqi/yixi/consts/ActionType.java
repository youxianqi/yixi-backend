package youxianqi.yixi.consts;

import java.util.HashMap;
import java.util.Map;

public enum ActionType {
    NON(0), BUY(1), VIEW(2), TAG(3),LIKE(4),FAV(5),COMMENT(6);

    private int value;
    private static Map map = new HashMap<>();

    private ActionType(int value) {
        this.value = value;
    }

    static {
        for (ActionType type : ActionType.values()) {
            map.put(type.value, type);
        }
    }

    public static ActionType valueOf(int type) {
        return (ActionType) map.get(type);
    }
    public int getValue() {
        return value;
    }
}
