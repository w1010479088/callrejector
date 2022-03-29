package com.bruceewu.callrejector.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.bruceewu.callrejector.App;
import com.bruceewu.callrejector.R;

public class NotificationHelper {
    private static int DOWNLOAD_NOTIFICATION_ID = 0;

    public static void show(Context context, String title) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = newBuilder(notificationManager);

        builder.setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentText(title)
                .setTicker(title);
        builder.setSmallIcon(R.drawable.ic_launcher);
        notificationManager.notify(DOWNLOAD_NOTIFICATION_ID++, builder.build());
        if (DOWNLOAD_NOTIFICATION_ID > 100) {
            DOWNLOAD_NOTIFICATION_ID = 0;
        }
    }

    private static NotificationCompat.Builder newBuilder(NotificationManager manager) {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = createNotificationChannel(manager);
            builder = new NotificationCompat.Builder(App.getInstance(), channelId);
        } else {
            builder = new NotificationCompat.Builder(App.getInstance());
        }
        return builder;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private static String createNotificationChannel(NotificationManager manager) {
        String name = "call_service";
        String id = "call_rejector";
        int importance = NotificationManager.IMPORTANCE_LOW;

        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.enableLights(false); // 不显示呼吸灯
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET); // 不在安全锁屏界面上显示此通知
        manager.createNotificationChannel(channel);
        return id;
    }
}
