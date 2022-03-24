package com.bruceewu.callrejector.utils;

import android.util.Log;

import com.bruceewu.callrejector.BuildConfig;

public class LogUtils {
    public static void log(String content) {
        if (BuildConfig.DEBUG) {
            Log.d("tag_log_xyf", content);
        }
    }
}
