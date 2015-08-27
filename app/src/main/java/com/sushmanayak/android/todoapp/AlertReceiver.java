package com.sushmanayak.android.todoapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

/**
 * Created by SushmaNayak on 8/27/2015.
 */
public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String msgText = context.getResources().getString(R.string.taskDue);
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            msgText = intent.getStringExtra(Intent.EXTRA_TEXT);
        }
        createNotification(context, context.getResources().getString(R.string.taskDue), msgText, "Alert");
    }

    private void createNotification(Context context, String msg, String msgText, String title) {
        PendingIntent notifIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_due)
                .setContentTitle(msg)
                .setTicker(title)
                .setContentText(msgText);

        mBuilder.setContentIntent(notifIntent);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationMgr.notify(0, mBuilder.build());
    }
}
