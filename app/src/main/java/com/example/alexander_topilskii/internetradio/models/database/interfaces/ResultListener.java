package com.example.alexander_topilskii.internetradio.models.database.interfaces;


import android.database.Cursor;

import com.example.alexander_topilskii.internetradio.models.database.Station;

public interface ResultListener {
    void stationsResult(Cursor cursor);
    void currentStationResult(Station station);
}
