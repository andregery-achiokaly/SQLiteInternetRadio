package com.example.alexander_topilskii.internetradio.ui.interfaces;


import com.example.alexander_topilskii.internetradio.models.database.Station;

public interface AdapterCallback {
    void itemClick(Station station);
    void itemLongClick(Station station);
}
