package com.bruceewu.callrejector.utils;

import android.content.Context;
import android.widget.Toast;

import com.bruceewu.callrejector.App;

public class ToastUtils {
    private static Context context;

    static {
        ToastUtils.context = App.getInstance();
    }

    public static void show(String content) {
        Toast.makeText(context, content, Toast.LENGTH_LONG).show();
    }
}
