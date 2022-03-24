package com.bruceewu.callrejector.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import com.android.internal.telephony.ITelephony;

import java.io.IOException;
import java.lang.reflect.Method;

public class CallService extends Service {

    private TelephonyManager manager;
    private final CallListener listener = new CallListener(new CallListener.Listener() {
        @Override
        public void onRing(String mobile) {
            LogUtils.log(mobile);
            LogUtils.log(String.format("onRing = %s", mobile));
//            rejectCall();
        }

        @Override
        public void onLine(String mobile) {
            LogUtils.log(String.format("onLine = %s", mobile));
        }

        @Override
        public void onIdle(String mobile) {
            LogUtils.log(String.format("onIdle = %s", mobile));
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
                IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
                ITelephony telephony = ITelephony.Stub.asInterface(binder);
                telephony.endCall();
            } catch (Exception e) {
                LogUtils.log(e.getMessage());
            }
        } else {
            TelephonyManager mTelMgr = (TelephonyManager) getSystemService(Service.TELEPHONY_SERVICE);
            Class<TelephonyManager> c = TelephonyManager.class;
            try {
                Method getITelephonyMethod = c.getDeclaredMethod("getITelephony", (Class[]) null);
                getITelephonyMethod.setAccessible(true);
                ITelephony iTelephony = null;
                LogUtils.log("End call.");
                iTelephony = (ITelephony) getITelephonyMethod.invoke(mTelMgr, (Object[]) null);
                iTelephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
                LogUtils.log("Fail to answer ring call.");
            }
        }
    }

    /**
     * 接听电话
     */
    private void acceptCall() {
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.answerRingingCall();
        } catch (Exception e) {
            LogUtils.log("for version 4.1 or larger");
            acceptCall_4_1();
        }
    }

    /**
     * 4.1版本以上接听电话
     */
    private void acceptCall_4_1() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //模拟无线耳机的按键来接听电话
        // for HTC devices we need to broadcast a connected headset
        boolean broadcastConnected = "htc".equalsIgnoreCase(Build.MANUFACTURER)
                && !audioManager.isWiredHeadsetOn();
        if (broadcastConnected) {
            broadcastHeadsetConnected(false);
        }
        try {
            try {
                Runtime.getRuntime().exec("input keyevent " +
                        KeyEvent.KEYCODE_HEADSETHOOK);
            } catch (IOException e) {
                // Runtime.exec(String) had an I/O problem, try to fall back
                String enforcedPerm = "android.permission.CALL_PRIVILEGED";
                Intent btnDown = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
                Intent btnUp = new Intent(Intent.ACTION_MEDIA_BUTTON).putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                sendOrderedBroadcast(btnDown, enforcedPerm);
                sendOrderedBroadcast(btnUp, enforcedPerm);
            }
        } finally {
            if (broadcastConnected) {
                broadcastHeadsetConnected(false);
            }
        }
    }

    private void broadcastHeadsetConnected(boolean connected) {
        Intent i = new Intent(Intent.ACTION_HEADSET_PLUG);
        i.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
        i.putExtra("state", connected ? 1 : 0);
        i.putExtra("name", "mysms");
        try {
            sendOrderedBroadcast(i, null);
        } catch (Exception e) {
        }
    }
}
