package com.example.alexander_topilskii.internetradio.presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.DataBaseChangedListener;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.ResultListener;
import com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SqliteExecutorManager;
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

public class BasePresenter extends MvpBasePresenter<BaseActivity> implements BasePresenterInterface {
    private static final String TAG_CHANGE_STATION_DIALOG = "TAG_CHANGE_STATION_DIALOG";
    private static final String TAG_ADD_STATION_DIALOG = "TAG_ADD_STATION_DIALOG";
    private static final String TAG_EDIT_STATION_DIALOG = "TAG_EDIT_STATION_DIALOG";

    private RadioServiceConnection radioServiceConnection;
    private PlayerCallbackListener playerCallbackListener;
    private RadioVisualizer radioVisualizer;
    private Station currentStation;
    private SqliteExecutorManager dataBase;
    private Player player;
    private boolean canShow;
    private OnChangeDialogResultListener onDialogResultListener;

    public BasePresenter(Context context) {
        radioServiceConnection = new RadioServiceConnection();
        dataBase = SqliteExecutorManager.getInstance(context);
        DataBaseChangedListener dataBaseChangeListener = getDataBaseChangeListener();
        ResultListener resultListener = getResultListener();
        dataBase.addChangeListener(dataBaseChangeListener);
        dataBase.addResultListener(resultListener);
        playerCallbackListener = getPlayerCallbackListener();
        radioVisualizer = new RadioVisualizer();
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
        return () -> dataBase.getStations();
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
                dataBase.deleteStation(id);
            }

            @Override
            public void onEditResult(int id, String name, String source) {
                if (context != null) {
                    EditStationDialog editStationDialog = EditStationDialog.newInstance(id, name, source);
                    editStationDialog.setOnChangeDialogResultListener((id1, name1, source1) -> dataBase.editStation(id, name, source));
                    editStationDialog.show(((MainActivity) context).getSupportFragmentManager(), TAG_EDIT_STATION_DIALOG);
                }
            }
        };
    }

    private PlayerCallbackListener getPlayerCallbackListener() {
        return (id, state) -> {
            if (getView() != null) {
                getView().changeState(state);
                if (canShow) {
                    if (radioVisualizer != null && player != null) radioVisualizer.setupVisualizerFxAndUI(player.getId(), bytes -> {
                        if (bytes != null) getView().setAudioWave(bytes);
                    });
                }
            }
        };
    }

    @Override
    public void stationClick(Station station) {
        dataBase.changeCurrentStations(station.getId());
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
        if (currentStation != null && getView() != null) getView().changeState(State.IS_ERROR, "Chose a station!");
        else if (player != null) player.changeState(currentStation);
    }

    @Override
    public void onResume(Context context) {
        dataBase.getCurrentStation();
        bindToRadioService(context);
        dataBase.getStations();
    }

    private void bindToRadioService(Context context) {
        Intent intent = new Intent(context, PlayerService.class);
        context.bindService(intent, radioServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause(Context context) {
        radioVisualizer.stop();
        if (player != null) player.setPlayerCallbackListener(null);
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
        changeStationDialog.setOnAddDialogResultListener((name, source) -> dataBase.addStation(name, source));
        changeStationDialog.show(activity.getSupportFragmentManager(), TAG_ADD_STATION_DIALOG);
    }

    private class RadioServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            ((PlayerService.RadioBinder) binder).setPlayerCallbackListener(playerCallbackListener);
            player = ((PlayerService.RadioBinder) binder);
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
