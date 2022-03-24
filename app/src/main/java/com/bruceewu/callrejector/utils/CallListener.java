package com.bruceewu.callrejector.utils;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class CallListener extends PhoneStateListener {
    private final Listener listener;

    public CallListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void onCallStateChanged(int state, String mobile) {
        super.onCallStateChanged(state, mobile);
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING://响铃状态
                listener.onRing(mobile);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK://通话状态,没有标记呼入还是呼出
                break;
            case TelephonyManager.CALL_STATE_IDLE://空闲状态，没有区分是挂断空闲，还是没有通话的空闲
                break;
        }
    }

    public interface Listener {
        void onRing(String mobile);
    }
}
