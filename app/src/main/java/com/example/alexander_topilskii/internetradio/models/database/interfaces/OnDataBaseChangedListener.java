package com.example.alexander_topilskii.internetradio.models.database.interfaces;


import com.example.alexander_topilskii.internetradio.models.database.NoStationsException;

public interface OnDataBaseChangedListener {
    void onDataBaseChanged() throws NoStationsException;
}
