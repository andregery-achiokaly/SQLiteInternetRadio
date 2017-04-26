package com.example.alexander_topilskii.internetradio.presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.alexander_topilskii.internetradio.Application;
import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.DataBaseChangedListener;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.DataBaseManager;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.ResultListener;
import com.example.alexander_topilskii.internetradio.models.player.PlayerService;
import com.example.alexander_topilskii.internetradio.models.player.State;
import com.example.alexander_topilskii.internetradio.models.player.interfaces.Player;
import com.example.alexander_topilskii.internetradio.models.player.interfaces.PlayerCallbackListener;
import com.example.alexander_topilskii.internetradio.models.vizualizer.RadioVisualizer;
import com.example.alexander_topilskii.internetradio.presenters.interfaces.BasePresenterInterface;
import com.example.alexander_topilskii.internetradio.presenters.interfaces.OnChangeDialogResultListener;
import com.example.alexander_topilskii.internetradio.ui.activitys.MainActivity;
import com.example.alexander_topilskii.internetradio.ui.dialog.AddStationDialog;
import com.example.alexander_topilskii.internetradio.ui.dialog.ChangeStationDialog;
import com.example.alexander_topilskii.internetradio.ui.dialog.EditStationDialog;
import com.example.alexander_topilskii.internetradio.ui.interfaces.BaseActivity;
import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import javax.inject.Inject;

public class BasePresenter extends MvpBasePresenter<BaseActivity> implements BasePresenterInterface {
    private static final String TAG_CHANGE_STATION_DIALOG = "TAG_CHANGE_STATION_DIALOG";
    private static final String TAG_ADD_STATION_DIALOG = "TAG_ADD_STATION_DIALOG";
    private static final String TAG_EDIT_STATION_DIALOG = "TAG_EDIT_STATION_DIALOG";

    @Inject
    DataBaseManager dataBaseManager;
    @Inject
    Player player;
    @Inject
    RadioVisualizer radioVisualizer;

    private RadioServiceConnection radioServiceConnection;
    private PlayerCallbackListener playerCallbackListener;
    private Station currentStation;
    private boolean canShow;
    private OnChangeDialogResultListener onDialogResultListener;
    private DataBaseChangedListener dataBaseChangeListener;
    private ResultListener resultListener;

    public BasePresenter(Context context) {
        Application.getComponent().injectBasePresenter(this);
        radioServiceConnection = new RadioServiceConnection();
        dataBaseChangeListener = getDataBaseChangeListener();
        resultListener = getResultListener();
        dataBaseManager.addChangeListener(dataBaseChangeListener);
        dataBaseManager.addResultListener(resultListener);
        playerCallbackListener = getPlayerCallbackListener();
        onDialogResultListener = getOnDialogResultListener(context);
    }

    @NonNull
    private ResultListener getResultListener() {
        return new ResultListener() {
            @Override
            public void stationsResult(Cursor cursor) {
                if (getView() != null) {
                    getView().setListStation(cursor);
                }
            }

            @Override
            public void currentStationResult(Station station) {
                currentStation = station;
            }
        };
    }


    @NonNull
    private DataBaseChangedListener getDataBaseChangeListener() {
        return () -> dataBaseManager.getStations();
    }

    @NonNull
    private OnChangeDialogResultListener getOnDialogResultListener(final Context context) {
        return new OnChangeDialogResultListener() {
            @Override
            public void onShareResult(String name, String source) {
                if (context != null) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "It is my favorite Radio: " + name + "\n" + source);
                    sendIntent.setType("text/plain");
                    context.startActivity(sendIntent);
                }
            }

            @Override
            public void onDeleteResult(int id) {
                dataBaseManager.deleteStation(id);
            }

            @Override
            public void onEditResult(int id, String name, String source) {
                if (context != null) {
                    EditStationDialog editStationDialog = EditStationDialog.newInstance(id, name, source);
                    editStationDialog.setOnChangeDialogResultListener((id1, name1, source1) -> dataBaseManager.editStation(id, name, source));
                    editStationDialog.show(((MainActivity) context).getSupportFragmentManager(), TAG_EDIT_STATION_DIALOG);
                }
            }
        };
    }

    private PlayerCallbackListener getPlayerCallbackListener() {
        return (id, state) -> {
            if (getView() != null) getView().changeState(state);
            if (radioVisualizer != null && player != null && canShow) {
                radioVisualizer.setupVisualizerFxAndUI(player.getId(), bytes -> {
                    if (bytes != null && getView() != null) getView().setAudioWave(bytes);
                });
            }
        };
    }

    @Override
    public void stationClick(Station station) {
        dataBaseManager.changeCurrentStations(station.getId());
        if (player != null) {
            if (currentStation == station) {
                player.changeState(currentStation);
            } else player.setNewStation(station);
        }
        currentStation = station;
    }

    @Override
    public void stationLongClick(MainActivity activity, Station station) {
        ChangeStationDialog changeStationDialog = ChangeStationDialog.newInstance(station.getId(), station.getName(), station.getSource());
        changeStationDialog.setOnDialogResultListener(onDialogResultListener);
        changeStationDialog.show(activity.getSupportFragmentManager(), TAG_CHANGE_STATION_DIALOG);
    }

    @Override
    public void playButtonClick() {
        dataBaseManager.getCurrentStation();

        if (currentStation == null && getView() != null) getView().changeState(State.IS_ERROR, "Chose a station!");
        else if (player != null) player.changeState(currentStation);
    }

    @Override
    public void onResume(Context context) {
        dataBaseManager.getCurrentStation();
        bindToRadioService(context);
        dataBaseManager.getStations();
    }

    private void bindToRadioService(Context context) {
        Intent intent = new Intent(context, PlayerService.class);
        context.bindService(intent, radioServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause(Context context) {
        radioVisualizer.stop();
        dataBaseManager.deleteListeners(dataBaseChangeListener, resultListener);
        if (player != null) player.deletePlayerCallbackListener(playerCallbackListener);
        context.unbindService(radioServiceConnection);
        onDialogResultListener = null;
    }

    @Override
    public void setCanShowVizualizer(boolean canShow) {
        this.canShow = canShow;
    }

    @Override
    public void addStationClick(MainActivity activity) {
        AddStationDialog changeStationDialog = AddStationDialog.newInstance();
        changeStationDialog.setOnAddDialogResultListener((name, source) -> dataBaseManager.addStation(name, source));
        changeStationDialog.show(activity.getSupportFragmentManager(), TAG_ADD_STATION_DIALOG);
    }

    private class RadioServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            ((PlayerService.RadioBinder) binder).addPlayerCallbackListener(playerCallbackListener);
            if (getView() != null && player != null) {
                radioVisualizer.setupVisualizerFxAndUI(player.getId(), bytes -> getView().setAudioWave(bytes));
                getView().changeState(player.getState());
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            radioVisualizer.stop();
        }
    }
}
