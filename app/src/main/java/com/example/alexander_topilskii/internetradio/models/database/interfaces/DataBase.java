package com.example.alexander_topilskii.internetradio.models.database.interfaces;


import android.database.Cursor;

import com.example.alexander_topilskii.internetradio.models.database.Station;

public interface DataBase {
    Cursor getStations();

    void changeCurrentStations(int id);

    void deleteStation(int id);

    void addStation(String name, String source);

    Station getCurrentStation();

    Station getStation(int id);

    void editStation(int id, String name, String source);
}
