package com.example.alexander_topilskii.internetradio.models.player;


import com.example.alexander_topilskii.internetradio.R;

public enum State {
    IS_PLAY(R.mipmap.ic_pause_black_24dp),
    IS_STOP(R.mipmap.ic_play_arrow_black_24dp),
    IS_ERROR(R.mipmap.ic_error_black_24dp),
    IS_WAIT(R.mipmap.ic_history_black_24dp);

    State(int resourceBitmap) {
        this.resourceBitmap = resourceBitmap;
    }

    public int getResourceBitmap() {
        return resourceBitmap;
    }

    int resourceBitmap;

    @Override
    public String toString() {
        String state = "";
        switch (this) {
            case IS_PLAY:
                state = "stop";
                break;
            case IS_STOP:
                state = "play";
                break;
            case IS_ERROR:
                state = "error";
                break;
            case IS_WAIT:
                state = "wait";
                break;
        }
        return state;
    }
}
