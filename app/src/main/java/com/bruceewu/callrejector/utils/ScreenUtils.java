package com.bruceewu.callrejector.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.bruceewu.callrejector.App;


public class ScreenUtils {
    private static int screenWith;
    private static int screenHeight;

    public static int dip2px(int dip) {
        final float scale = App.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    public static int getScreenWidth() {
        if (screenWith == 0) {
            WindowManager windowManager = (WindowManager) App.getInstance().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();// 创建了一张白纸
            windowManager.getDefaultDisplay().getMetrics(dm);// 给白纸设置宽高
            screenWith = dm.widthPixels;
        }
        return screenWith;
    }

    public static int getScreenHeight() {
        if (screenHeight == 0) {
            WindowManager windowManager = (WindowManager) App.getInstance().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics dm = new DisplayMetrics();// 创建了一张白纸
            windowManager.getDefaultDisplay().getMetrics(dm);// 给白纸设置宽高
            screenHeight = dm.heightPixels;
        }
        return screenHeight;
    }
}
