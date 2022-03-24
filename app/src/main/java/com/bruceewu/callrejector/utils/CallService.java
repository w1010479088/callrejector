package com.bruceewu.callrejector.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class CallService extends Service {

    private TelephonyManager manager;
    private final CallListener listener = new CallListener(new CallListener.Listener() {
        @Override
        public void onRing(String mobile) {
            LogUtils.log(mobile);
            rejectCall();
        }
    });

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.log("onCreate");
        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        register();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.log("onDestroy");
        unRegister();
    }

    private void register() {
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void unRegister() {
        manager.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    /**
     * 拒绝接听
     */
    private void rejectCall() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                LogUtils.log("拒接开始0！");
                TelecomManager telecomManager = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
                telecomManager.endCall();
                LogUtils.log("拒接成功！");
            } catch (Exception ex) {
                LogUtils.log("拒接失败!" + ex.getMessage());
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                LogUtils.log("拒接开始1！");
                Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
                IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
                ITelephony telephony = ITelephony.Stub.asInterface(binder);
                telephony.endCall();
                LogUtils.log("拒接成功！");
            } catch (Exception e) {
                LogUtils.log("拒接失败！" + e.getMessage());
            }
        } else {
            try {
                LogUtils.log("拒接开始2！");
                TelephonyManager mTelMgr = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
                Class<TelephonyManager> c = TelephonyManager.class;
                Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
                getITelephonyMethod.setAccessible(true);
                ITelephony iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelMgr, (Object[]) null);
                iTelephony.endCall();
                LogUtils.log("拒接成功！");
            } catch (Exception e) {
                LogUtils.log("拒接失败！" + e.getMessage());
            }
        }
    }
}
