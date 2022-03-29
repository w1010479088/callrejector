package com.bruceewu.callrejector.business;

import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.bruceewu.callrejector.ui.NotificationHelper;
import com.bruceewu.callrejector.utils.DoubleClickHelper;
import com.bruceewu.callrejector.utils.LogUtils;
import com.bruceewu.callrejector.utils.SharePreferenceUtils;

import java.lang.reflect.Method;

import static android.content.Context.TELEPHONY_SERVICE;

public class CallRejector {
    private Context context;
    private final TelephonyManager manager;

    private final CallListener listener = new CallListener(mobile ->
            checkOpen(() -> {
                LogUtils.log(mobile);
                DoubleClickHelper.click("listener", () -> CallFilter.filter(mobile, () -> {
                    reject();
                    if (context != null) {
                        NotificationHelper.show(context, String.format("已帮您拦截号码：%s", mobile));
                    }
                }));
            }));

    public static void newInstance(Context context) {
        checkOpen(() -> DoubleClickHelper.click("newInstance", () -> new CallRejector(context)));
    }

    private CallRejector(Context context) {
        this.context = context;
        this.manager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        register();
    }

    //拒接
    private void reject() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            rejectP();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rejectM();
        } else {
            rejectLow();
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private void rejectP() {
        try {
            LogUtils.log("拒接开始0！");
            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            telecomManager.endCall();
            LogUtils.log("拒接成功！");
        } catch (Exception ex) {
            LogUtils.log("拒接失败!" + ex.getMessage());
        }
    }

    private void rejectM() {
        try {
            LogUtils.log("拒接开始1！");
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();
            LogUtils.log("拒接成功！");
        } catch (Exception e) {
            LogUtils.log("拒接失败！" + e.getMessage());
        }
    }

    private void rejectLow() {
        try {
            LogUtils.log("拒接开始2！");
            TelephonyManager mTelMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
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

    private static void checkOpen(Runnable next) {
        if (SharePreferenceUtils.needInterrupt()) {
            next.run();
        }
    }

    private void register() {
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    //暂且不需要
    private void unRegister() {
        manager.listen(listener, PhoneStateListener.LISTEN_NONE);
    }
}
