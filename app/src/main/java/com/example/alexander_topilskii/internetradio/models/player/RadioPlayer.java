package com.example.alexander_topilskii.internetradio.models.player;


import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;

import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.player.interfaces.PlayerCallbackListener;
import com.example.alexander_topilskii.internetradio.models.player.interfaces.Player;

import java.io.IOException;

class RadioPlayer implements Player, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private MediaPlayer mediaPlayer;
    private PlayerCallbackListener listener;
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
            listener.setPlayerStates(mediaPlayer.getAudioSessionId(), currentState);
        } catch (IOException | NullPointerException e) {
            errorProcessing(e);
        }
    }

    private void errorProcessing(Exception e) {
        currentState = State.IS_ERROR;
        listener.setPlayerStates(mediaPlayer.getAudioSessionId(), State.IS_ERROR);
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
        listener.setPlayerStates(mediaPlayer.getAudioSessionId(), State.IS_PLAY);
    }

    private void pause() {
        mediaPlayer.pause();
        // Wait 1 min. If user doesn't run player again - player will be closed.
        // If we don't close player - player will spend traffic
        radioCloseHandler.postDelayed(radioCloseRunnable, 60 * 1000);

        currentState = State.IS_STOP;
        listener.setPlayerStates(mediaPlayer.getAudioSessionId(), State.IS_STOP);
    }

    @Override
    public void changeState(Station station) {
        currentState = State.IS_WAIT;
        if (station != null) {
            listener.setPlayerStates(-1, State.IS_WAIT);
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
        listener.setPlayerStates(-1, State.IS_WAIT);
        if (mediaPlayer == null) {
            initPlayer(station.getSource());
        } else {
            changeSource(station.getSource());
        }
    }

    @Override
    public void close() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public void setPlayerCallbackListener(PlayerCallbackListener listener) {
        this.listener = listener;
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
            listener.setPlayerStates(mediaPlayer.getAudioSessionId(), State.IS_PLAY);
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        errorProcessing(new Exception());
        return true;
    }
}
