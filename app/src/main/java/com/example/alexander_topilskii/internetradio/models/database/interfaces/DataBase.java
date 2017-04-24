package com.example.alexander_topilskii.internetradio.models.database.interfaces;


import android.database.Cursor;

import com.example.alexander_topilskii.internetradio.models.database.NoStationsException;
import com.example.alexander_topilskii.internetradio.models.database.Station;

public interface DataBase {
    Cursor getStations() throws NoStationsException;
    void changeCurrentStations(int id) throws NoStationsException;
    void deleteStation(int id) throws NoStationsException;
    void addStation(String name, String source) throws NoStationsException;
    Station getCurrentStation() throws NoStationsException;
    Station getStation(int id) throws NoStationsException;
    void editStation(int id, String name, String source) throws NoStationsException;
    void closeDataBase();
}
