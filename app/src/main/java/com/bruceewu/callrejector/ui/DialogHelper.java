package com.bruceewu.callrejector.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.webkit.ValueCallback;

public class DialogHelper {

    public static void show(Context context, String title, Runnable confirm) {
        new AlertDialog
                .Builder(context)
                .setTitle(title)
                .setPositiveButton("确认", (dialog, which) -> confirm.run())
                .setNegativeButton("取消", (dialog, which) -> {

                })
                .create()
                .show();
    }

    public static void add(Context context, ValueCallback<String> listener) {
        InputDialog.show(context, listener);
    }
}
