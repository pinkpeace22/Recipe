package kr.ac.hs.recipe.dialog;

import java.util.HashMap;

public enum TimerType {
    TIMER_1(0),
    TIMER_2(0),
    TIMER_3(0);

    long remain;


    static public final HashMap<TimerType, Long> sItemRowMap;

    static {
        sItemRowMap = new HashMap<>(TimerType.values().length);
        for (TimerType type : TimerType.values()) {
            sItemRowMap.put(type, type.remain);
        }
    }

    TimerType(long remainAmount) {
        this.remain = remainAmount;
    }

    public static Long byRemainTime(TimerType timerPreSet) {
        return sItemRowMap.get(timerPreSet);
    }

}
