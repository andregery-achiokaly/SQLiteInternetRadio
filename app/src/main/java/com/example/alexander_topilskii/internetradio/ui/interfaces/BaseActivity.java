package com.example.alexander_topilskii.internetradio.ui.interfaces;


import android.database.Cursor;

import com.example.alexander_topilskii.internetradio.models.player.State;
import com.hannesdorfmann.mosby.mvp.MvpView;

public interface BaseActivity extends MvpView {
    void setListStation(Cursor stations);
    void setAudioWave(byte[] bytes);
    void changeState(State state);
}
