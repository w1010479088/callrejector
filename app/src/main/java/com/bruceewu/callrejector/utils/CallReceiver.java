package com.bruceewu.callrejector.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!TextUtils.equals(intent.getAction(), Intent.ACTION_NEW_OUTGOING_CALL)) {
            new CallRejector(context);
        }
    }
}
