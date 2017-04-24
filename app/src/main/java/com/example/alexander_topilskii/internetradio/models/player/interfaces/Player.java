package com.example.alexander_topilskii.internetradio.models.player.interfaces;


import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.player.State;

public interface Player {
    void changeState(Station station);
    void setNewStation(Station station);
    void close();
    void setPlayerCallbackListener(PlayerCallbackListener listener);
    int getId();
    State getState();
}
