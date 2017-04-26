package com.example.alexander_topilskii.internetradio.ui.notification;


import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.example.alexander_topilskii.internetradio.R;
import com.example.alexander_topilskii.internetradio.models.player.PlayerService;
import com.example.alexander_topilskii.internetradio.ui.activitys.MainPermissionActivity;

public class RadioNotification {
    private static final int PENDING_INTENT_CODE = 755;
    public final static int ID = 123;
    private final Notification notification;
    public final static String ACTION = "ACTION";
    public final static String PLAY = "PLAY";

    public RadioNotification(Context context, String radioState) {
        Intent playRadioIntent = new Intent(context, PlayerService.class);
        playRadioIntent.putExtra(ACTION, PLAY);

        PendingIntent changeRadioStateIntent = PendingIntent.getService(context, PENDING_INTENT_CODE, playRadioIntent, 0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_radio_black_24dp)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentTitle("Great Radio")
                        .addAction(R.mipmap.ic_radio_black_24dp, radioState, changeRadioStateIntent)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, MainPermissionActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainPermissionActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent);
        notification = builder.build();
    }

    public Notification getNotification() {
        return notification;
    }
}
