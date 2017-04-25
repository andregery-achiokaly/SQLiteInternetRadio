package com.example.alexander_topilskii.internetradio.models.player;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.DataBase;
import com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SqliteExecutorManager;
import com.example.alexander_topilskii.internetradio.models.player.interfaces.Player;
import com.example.alexander_topilskii.internetradio.models.player.interfaces.PlayerCallbackListener;
import com.example.alexander_topilskii.internetradio.ui.notification.RadioNotification;

import static com.example.alexander_topilskii.internetradio.ui.notification.RadioNotification.ACTION;
import static com.example.alexander_topilskii.internetradio.ui.notification.RadioNotification.PLAY;

public class PlayerService extends Service {
    private Player player = new RadioPlayer();
    private Notification notification;
    private DataBase dataBase;

    @Override
    public void onCreate() {
        super.onCreate();
        dataBase = SqliteExecutorManager.getInstance(getApplicationContext());
        notification = new RadioNotification(getApplicationContext(), "Radio").getNotification();
        startForeground(RadioNotification.ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (intent != null) {
            String action = intent.getStringExtra(ACTION);
            if (action != null && action.equals(PLAY)) {
                player.changeState(dataBase.getCurrentStation());
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        player.close();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new RadioBinder();
    }

    public class RadioBinder extends Binder implements Player {
        public void setPlayerCallbackListener(PlayerCallbackListener listener) {
            PlayerCallbackListener playerCallbackListener = (id, state) -> {
                if (listener != null) listener.setPlayerStates(id, (state));
                notification = new RadioNotification(getApplicationContext(), state.toString()).getNotification();
                NotificationManager mNotificationManager = (NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.notify(RadioNotification.ID, notification);
            };

            player.setPlayerCallbackListener(playerCallbackListener);
        }

        @Override
        public void changeState(Station station) {
            player.changeState(station);
        }

        @Override
        public void setNewStation(Station station) {
            player.setNewStation(station);
        }

        @Override
        public void close() {
            player.close();
        }

        @Override
        public int getId() {
            return player.getId();
        }

        @Override
        public State getState() {
            return player.getState();
        }
    }
}
