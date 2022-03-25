package com.bruceewu.callrejector.utils;

import java.util.HashMap;
import java.util.Map;

public class DoubleClickHelper {
    private static final long TIME_PERIOD = 1000;
    private static final Map<String, Long> times = new HashMap<>();

    public static void click(String tag, Runnable next) {
        long curTime = System.currentTimeMillis();
        Long preTimeObj = times.get(tag);
        long preTime = preTimeObj == null ? 0L : preTimeObj;
        if (curTime - preTime > TIME_PERIOD) {
            times.put(tag, curTime);
            next.run();
        }
    }
}
