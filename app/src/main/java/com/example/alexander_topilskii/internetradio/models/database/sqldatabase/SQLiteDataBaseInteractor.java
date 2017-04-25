package com.example.alexander_topilskii.internetradio.models.database.sqldatabase;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.OnDataBaseChangedListener;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.ResultListener;

public class SQLiteDataBaseInteractor implements LoaderManager.LoaderCallbacks<Object> {
    static final String DELETE_ID = "DELETE_ID";
    static final String CURRENT_ID = "CURRENT_ID";
    static final String ADD_SOURCE = "ADD_SOURCE";
    static final String ADD_NAME = "ADD_NAME";
    static final String STATION_ID = "STATION_ID";
    static final String EDIT_ID = "EDIT_ID";
    static final String EDIT_NAME = "EDIT_NAME";
    static final String EDIT_SOURCE = "EDIT_SOURCE";
    private Context context;
    private LoaderManager loaderManager;
    private OnDataBaseChangedListener onDataBaseChangedListener;
    private ResultListener resultListener;

    static final int GET_STATIONS_ID = 1;
    static final int CHANGE_CURRENT_STATION_ID = 2;
    static final int DELETE_STATION_ID = 3;
    static final int ADD_STATION_ID = 4;
    static final int GET_CURRENT_STATION_ID = 5;
    static final int EDIT_STATION_ID = 6;

    public SQLiteDataBaseInteractor(Context context, LoaderManager loaderManager, ResultListener resultListener, OnDataBaseChangedListener onDataBaseChangedListener) {
        this.context = context;
        this.loaderManager = loaderManager;
        this.resultListener = resultListener;
        this.onDataBaseChangedListener = onDataBaseChangedListener;
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new SQLDataBaseLoader(context, id, args);
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        switch (loader.getId()) {
            case GET_STATIONS_ID:
                resultListener.stationsResult((Cursor) data);
                break;
            case CHANGE_CURRENT_STATION_ID:
                onDataBaseChangedListener.onDataBaseChanged();
                break;
            case DELETE_STATION_ID:
                onDataBaseChangedListener.onDataBaseChanged();
                break;
            case ADD_STATION_ID:
                onDataBaseChangedListener.onDataBaseChanged();
                break;
            case GET_CURRENT_STATION_ID:
                resultListener.currentStationResult((Station) data);
                break;
            case EDIT_STATION_ID:
                onDataBaseChangedListener.onDataBaseChanged();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }

    public void getStations() {
        loaderManager.restartLoader(GET_STATIONS_ID, null, this);
    }

    public   void changeCurrentStations(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt(CURRENT_ID, id);
        loaderManager.restartLoader(CHANGE_CURRENT_STATION_ID, bundle, this);
    }

    public void deleteStation(int id) {
        Bundle bundle = new Bundle();
        bundle.putInt(DELETE_ID, id);
        loaderManager.restartLoader(DELETE_STATION_ID, bundle, this);
    }

    public void addStation(String name, String source) {
        Bundle bundle = new Bundle();
        bundle.putString(ADD_NAME, name);
        bundle.putString(ADD_SOURCE, source);

        loaderManager.restartLoader(ADD_STATION_ID, bundle, this);
    }

    public void getCurrentStation() {
        loaderManager.restartLoader(GET_CURRENT_STATION_ID, null, this);
    }

    public  void editStation(int id, String name, String source) {
        Bundle bundle = new Bundle();
        bundle.putInt(EDIT_ID, id);
        bundle.putString(EDIT_NAME, name);
        bundle.putString(EDIT_SOURCE, source);
        loaderManager.restartLoader(EDIT_STATION_ID, bundle, this);
    }
}
