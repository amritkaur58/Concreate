package com.dailyreporting.app.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.dailyreporting.app.activities.CompleteDailyReportActivity;
import com.orhanobut.logger.Logger;

import static com.dailyreporting.app.Services.NotifyService.NOTIFICATION_CHANNEL_ID;

public class MyNotificationPublisher extends BroadcastReceiver {
    public static final String NOTIFICATION_ID = "notification-id";
    public static final String NOTIFICATION = "notification";

    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
            try {
                Intent intent1 = new Intent(context, CompleteDailyReportActivity.class);
                context.startActivity(intent1);

            } catch (Exception e) {
                Logger.e(e.getMessage());
            }
        }
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        assert notificationManager != null;
        notificationManager.notify(id, notification);
    }
}
