package com.example.alexander_topilskii.internetradio;


import android.app.Notification;

import com.example.alexander_topilskii.internetradio.models.database.interfaces.DataBase;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.DataBaseChangedListener;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.DataBaseManager;
import com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLDataBaseHelper;
import com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SqliteExecutorManager;
import com.example.alexander_topilskii.internetradio.models.player.RadioPlayer;
import com.example.alexander_topilskii.internetradio.models.player.interfaces.Player;
import com.example.alexander_topilskii.internetradio.models.rest.RestApi;
import com.example.alexander_topilskii.internetradio.models.vizualizer.RadioVisualizer;
import com.example.alexander_topilskii.internetradio.ui.notification.RadioNotification;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@Singleton
public class RepositoryModule {
    private Application application;

    public RepositoryModule(Application application) {
        this.application = application;
    }

    @Provides
    RadioVisualizer provideVisualizerModel() {
        return new RadioVisualizer();
    }

    @Provides
    @Singleton
    Player providePlayer() {
        return new RadioPlayer();
    }

    @Provides
    @Singleton
    DataBaseManager provideDataBaseManager() {
        return SqliteExecutorManager.getInstance();
    }

    @Provides
    DataBaseChangedListener getDataBaseChangeListener(DataBaseManager dataBaseManager) {
        return dataBaseManager::getStations;
    }

    @Provides
    Notification provideNotification() {
        return new RadioNotification(application, "").getNotification();
    }

    @Provides
    @Singleton
    DataBase provideDataBase() {
        return new SQLDataBaseHelper(application);
    }

    @Provides
    ExecutorService provideSingleThreadExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    Retrofit getRetrofit() {
       return new Retrofit.Builder()
                .baseUrl("http://achievements17.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    RestApi getRestApi(Retrofit retrofit){
       return retrofit.create(RestApi.class);
    }
}
