package com.example.alexander_topilskii.internetradio;


import android.content.Intent;

import com.example.alexander_topilskii.internetradio.models.player.PlayerService;
import com.example.alexander_topilskii.internetradio.models.rest.RestApi;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class Application extends android.app.Application {
    static AppComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component = DaggerAppComponent.builder().repositoryModule(new RepositoryModule(Application.this)).build();
        startService(new Intent(this, PlayerService.class));

    }

    public static AppComponent getComponent() {
        return component;
    }

    @Inject
    RestApi restApi;
    @Inject
    Retrofit retrofit;

}
