package com.example.alexander_topilskii.internetradio;


import android.content.Intent;

import com.example.alexander_topilskii.internetradio.models.player.PlayerService;

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, PlayerService.class));
    }
}
