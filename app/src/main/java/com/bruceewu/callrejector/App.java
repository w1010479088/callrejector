package com.bruceewu.callrejector;

import android.app.Application;
import android.content.Context;

import com.bruceewu.callrejector.ui.HolderConfigor;

public class App extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        HolderConfigor.init();
    }

    public static Context getInstance() {
        return context;
    }
}
