package com.bruceewu.callrejector.utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class CallService extends Service {
    private CallRejector rejector;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        rejector = new CallRejector(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (rejector != null) {
            rejector.unRegister();
        }
    }
}
