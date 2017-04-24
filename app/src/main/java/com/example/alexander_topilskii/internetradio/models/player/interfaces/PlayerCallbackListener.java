package com.example.alexander_topilskii.internetradio.models.player.interfaces;


import com.example.alexander_topilskii.internetradio.models.player.State;

public interface PlayerCallbackListener {
    void setPlayerStates(int id, State state);
}
