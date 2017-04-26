package com.example.alexander_topilskii.internetradio.models.player;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;

import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.player.interfaces.Player;
import com.example.alexander_topilskii.internetradio.models.player.interfaces.PlayerCallbackListener;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

class RadioPlayer implements Player, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private MediaPlayer mediaPlayer;
    private List<PlayerCallbackListener> callbackListeners;
    private Handler radioCloseHandler = new Handler();
    private Runnable radioCloseRunnable = this::close;
    private State currentState = State.IS_STOP;

    private void initPlayer(String source) {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setOnPreparedListener(this);
            mediaPlayer.setDataSource(source);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.prepareAsync();
            updateListeners(mediaPlayer.getAudioSessionId(), currentState);
        } catch (IOException | NullPointerException e) {
            errorProcessing(e);
        }
    }

    private void errorProcessing(Exception e) {
        currentState = State.IS_ERROR;
        updateListeners(mediaPlayer.getAudioSessionId(), State.IS_ERROR);
        e.printStackTrace();
        close();
    }

    private void changeSource(String source) {
        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.setDataSource(source);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            errorProcessing(e);
        }
    }

    private void play() {
        radioCloseHandler.removeCallbacks(radioCloseRunnable);
        mediaPlayer.start();
        currentState = State.IS_PLAY;
        updateListeners(mediaPlayer.getAudioSessionId(), State.IS_PLAY);
    }

    private void pause() {
        mediaPlayer.pause();
        // Wait 1 min. If user doesn't run player again - player will be closed.
        // If we don't close player - player will waste traffic
        radioCloseHandler.postDelayed(radioCloseRunnable, 60 * 1000);

        currentState = State.IS_STOP;
        updateListeners(mediaPlayer.getAudioSessionId(), State.IS_STOP);
    }

    @Override
    public void changeState(Station station) {
        currentState = State.IS_WAIT;
        updateListeners(-1, State.IS_WAIT);
        if (station != null) {
            if (mediaPlayer == null) initPlayer(station.getSource());
            else {
                if (mediaPlayer.isPlaying()) {
                    pause();
                } else {
                    play();
                }
            }
        }
    }

    @Override
    public void setNewStation(Station station) {
        currentState = State.IS_WAIT;
        updateListeners(-1, State.IS_WAIT);
        if (mediaPlayer == null) {
            initPlayer(station.getSource());
        } else {
            changeSource(station.getSource());
        }
    }

    private void updateListeners(int id, State isWait) {
        if (callbackListeners != null) {
            for (PlayerCallbackListener listener : callbackListeners) {
                listener.setPlayerStates(id, isWait);
            }
        }
    }

    @Override
    public void close() {
        callbackListeners = null;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void addPlayerCallbackListener(PlayerCallbackListener listener) {
        if (callbackListeners == null) callbackListeners = new LinkedList<>();
        this.callbackListeners.add(listener);
    }

    @Override
    public void deletePlayerCallbackListener(PlayerCallbackListener listener) {

    }

    @Override
    public int getId() {
        if (mediaPlayer != null) return mediaPlayer.getAudioSessionId();
        else return -1;
    }

    @Override
    public State getState() {
        return currentState;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        if (mp.isPlaying()) {
            currentState = State.IS_PLAY;
            updateListeners(mediaPlayer.getAudioSessionId(), State.IS_PLAY);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        errorProcessing(new Exception());
        return true;
    }
}
