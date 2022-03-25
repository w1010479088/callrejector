package com.bruceewu.callrejector.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.bruceewu.callrejector.App;


public class SharePreferenceUtils {
    private static final String KEY = "data";
    private static final SharedPreferences preferences;

    private static final String KEY_INTERRUPT = "need_interrupt";
    private static final String KEY_CALL_LIST = "call_list";

    static {
        preferences = App.getInstance().getSharedPreferences(KEY, Context.MODE_PRIVATE);
    }

    public static void setInterrupt(boolean interrupt) {
        put(KEY_INTERRUPT, interrupt);
    }

    public static boolean needInterrupt() {
        return getBool(KEY_INTERRUPT);
    }

    public static String getCallList() {
        return getString(KEY_CALL_LIST);
    }

    public static void setCallList(String value) {
        put(KEY_CALL_LIST, value);
    }

    //-------------------------------------我是分割线-------------------------------------------

    private static void put(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private static void put(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private static void put(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static String getString(String key) {
        return preferences.getString(key, null);
    }

    private static int getInt(String key) {
        return preferences.getInt(key, -1);
    }

    private static boolean getBool(String key) {
        return preferences.getBoolean(key, false);
    }
}
