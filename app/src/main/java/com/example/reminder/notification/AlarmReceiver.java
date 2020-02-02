package com.example.reminder.notification;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.reminder.R;
import com.example.reminder.application.FileAction;
import com.example.reminder.application.MainActivity;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra(MainActivity.EXTRA_TITLE);
        String content = intent.getStringExtra(MainActivity.EXTRA_CONTENT);
        String date = intent.getStringExtra(MainActivity.EXTRA_DATE);
        int id = intent.getIntExtra(MainActivity.EXTRA_ID, 0);

        Notification notification = new NotificationCompat.Builder(context, App.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(id, notification);
        FileAction.saveToArchive(context, title, content, date);
    }
}
