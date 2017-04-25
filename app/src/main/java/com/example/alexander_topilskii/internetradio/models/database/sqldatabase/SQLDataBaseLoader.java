package com.example.alexander_topilskii.internetradio.models.database.sqldatabase;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.ADD_NAME;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.ADD_SOURCE;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.ADD_STATION_ID;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.CHANGE_CURRENT_STATION_ID;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.CURRENT_ID;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.DELETE_ID;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.DELETE_STATION_ID;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.EDIT_ID;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.EDIT_NAME;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.EDIT_SOURCE;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.EDIT_STATION_ID;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.GET_CURRENT_STATION_ID;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.GET_STATION_BY_ID_ID;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.GET_STATION_ID;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor.STATION_ID;

public class SQLDataBaseLoader extends AsyncTaskLoader<Object> {
    private SQLDataBaseHelper db;
    private int type;
    private Bundle bundle;

    public SQLDataBaseLoader(Context context, int type, Bundle bundle) {
        super(context);
        db = new SQLDataBaseHelper(context);
        this.type = type;
        this.bundle = bundle;
    }

    @Override
    public Object loadInBackground() {
        switch (type) {
            case GET_STATION_ID: {
                return db.getStations();
            }
            case CHANGE_CURRENT_STATION_ID: {
                int currentId = bundle.getInt(CURRENT_ID);
                db.changeCurrentStations(currentId);
                return null;
            }
            case DELETE_STATION_ID: {
                int deleteId = bundle.getInt(DELETE_ID);
                db.deleteStation(deleteId);
                return null;
            }
            case ADD_STATION_ID: {
                String stationName = bundle.getString(ADD_NAME);
                String stationSource = bundle.getString(ADD_SOURCE);
                db.addStation(stationName, stationSource);
                return null;
            }
            case GET_CURRENT_STATION_ID: {
                return db.getCurrentStation();
            }
            case GET_STATION_BY_ID_ID: {
                int stationId = bundle.getInt(STATION_ID);
                return db.getStation(stationId);
            }
            case EDIT_STATION_ID: {
                int editId = bundle.getInt(EDIT_ID);
                String editNewName = bundle.getString(EDIT_NAME);
                String editNewSource = bundle.getString(EDIT_SOURCE);
                db.editStation(editId, editNewName, editNewSource);
                return null;
            }
            default:
                return null;
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


}
