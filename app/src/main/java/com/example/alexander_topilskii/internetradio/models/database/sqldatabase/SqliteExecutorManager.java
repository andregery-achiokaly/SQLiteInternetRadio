package com.example.alexander_topilskii.internetradio.models.database.sqldatabase;


import android.content.Context;
import android.database.Cursor;

import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.DataBaseChangedListener;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.DataBaseManager;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.ResultListener;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SqliteExecutorManager implements DataBaseManager {
    private ExecutorService executorService;
    private SQLDataBaseHelper dataBaseHelper;
    private List<DataBaseChangedListener> dataBaseChangedListenerList;
    private static SqliteExecutorManager sqliteExecutorManager;
    private List<ResultListener> resultListenerList;

    public static SqliteExecutorManager getInstance(Context context) {
        if (sqliteExecutorManager == null) {
            sqliteExecutorManager = new SqliteExecutorManager(context.getApplicationContext());
            return sqliteExecutorManager;
        } else return sqliteExecutorManager;
    }

    private SqliteExecutorManager(Context context) {
        executorService = Executors.newSingleThreadExecutor();
        dataBaseHelper = new SQLDataBaseHelper(context);
        dataBaseChangedListenerList = new LinkedList<>();
        resultListenerList = new LinkedList<>();
    }

    @Override
    public void deleteListeners(DataBaseChangedListener dataBaseChangeListener, ResultListener resultListener) {
        dataBaseChangedListenerList.remove(dataBaseChangeListener);
        resultListenerList.remove(resultListener);
    }

    public void addChangeListener(DataBaseChangedListener listener) {
        dataBaseChangedListenerList.add(listener);
    }

    public void addResultListener(ResultListener listener) {
        resultListenerList.add(listener);
    }

    public Cursor getStations() {
        executorService.submit(() -> {
            Cursor cursor = dataBaseHelper.getStations();
            for (ResultListener listener : resultListenerList) listener.stationsResult(cursor);
        });
        return null;
    }

    public void changeCurrentStations(int id) {
        executorService.submit(() -> {
            dataBaseHelper.changeCurrentStations(id);
            for (DataBaseChangedListener listener : dataBaseChangedListenerList) listener.onDataBaseChanged();
        });
    }

    public void deleteStation(int id) {
        executorService.submit(() -> {
            dataBaseHelper.deleteStation(id);
            for (DataBaseChangedListener listener : dataBaseChangedListenerList) listener.onDataBaseChanged();
        });

    }

    public void addStation(String name, String source) {
        executorService.submit(() -> {
            dataBaseHelper.addStation(name, source);
            for (DataBaseChangedListener listener : dataBaseChangedListenerList) listener.onDataBaseChanged();
        });
    }

    public Station getCurrentStation() {
        executorService.submit(() -> {
            Station station = dataBaseHelper.getCurrentStation();
            for (ResultListener listener : resultListenerList) listener.currentStationResult(station);
        });
        return null;
    }

    public void editStation(int id, String name, String source) {
        executorService.submit(() -> {
            dataBaseHelper.editStation(id, name, source);
            for (DataBaseChangedListener listener : dataBaseChangedListenerList) listener.onDataBaseChanged();
        });
    }

    @Override
    public void closeDataBase() {
        dataBaseChangedListenerList.clear();
        resultListenerList.clear();
        executorService.shutdownNow();
        executorService.submit(() -> dataBaseHelper.closeDataBase());
    }
}
