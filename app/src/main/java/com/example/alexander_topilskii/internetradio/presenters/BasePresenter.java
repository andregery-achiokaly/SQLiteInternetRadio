package com.example.alexander_topilskii.internetradio.presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.example.alexander_topilskii.internetradio.models.database.NoStationsException;
import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.DataBase;
import com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLDataBase;
import com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLDataBaseHelper;
import com.example.alexander_topilskii.internetradio.models.player.PlayerService;
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
    private DataBase dataBase;
    private Player player;
    private boolean canShow;
    private OnChangeDialogResultListener onDialogResultListener;

    public BasePresenter(Context context) {
        radioServiceConnection = new RadioServiceConnection();
        dataBase = new SQLDataBase(new SQLDataBaseHelper(context).getReadableDatabase());
        playerCallbackListener = getPlayerCallbackListener();
        radioVisualizer = new RadioVisualizer();
        onDialogResultListener = getOnDialogResultListener(((MainActivity) context).getSupportFragmentManager());
    }

    @NonNull
    private OnChangeDialogResultListener getOnDialogResultListener(final FragmentManager fragmentManager) {
        return new OnChangeDialogResultListener() {
            @Override
            public void onShareResult(int id) {

            }

            @Override
            public void onDeleteResult(int id) {
                dataBase.deleteStation(id);
            }

            @Override
            public void onEditResult(int id) {
                try {
                    Station station = dataBase.getStation(id);
                    EditStationDialog editStationDialog = EditStationDialog.newInstance(id, station.getName(), station.getSource());
                    editStationDialog.setOnChangeDialogResultListener((id1, name, source) -> dataBase.editStation(id, name, source));
                    editStationDialog.show(fragmentManager, TAG_EDIT_STATION_DIALOG);
                } catch (NoStationsException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private PlayerCallbackListener getPlayerCallbackListener() {
        return (id, state) -> {
            if (getView() != null) {
                if (canShow && (player != null)) {
                    getView().changeState(state);
                    if (radioVisualizer != null) radioVisualizer.setupVisualizerFxAndUI(player.getId(), bytes -> getView().setAudioWave(bytes));
                }
            }
        };
    }

    @Override
    public void stationClick(Station station) {
        dataBase.changeCurrentStations(station.getId());
        if (player != null) {
            if (currentStation == station) player.changeState(currentStation);
            else player.setNewStation(station);
        }
        currentStation = station;
    }

    @Override
    public void stationLongClick(MainActivity activity, Station station) {
        ChangeStationDialog changeStationDialog = ChangeStationDialog.newInstance(station.getId());
        changeStationDialog.setOnDialogResultListener(onDialogResultListener);
        changeStationDialog.show(activity.getSupportFragmentManager(), TAG_CHANGE_STATION_DIALOG);
    }

    @Override
    public void playButtonClick() {
        if (player != null) player.changeState(currentStation);
    }

    @Override
    public void onResume(Context context) {
        try {
            currentStation = dataBase.getCurrentStation();
        } catch (NoStationsException e) {
            e.printStackTrace();
        }
        bindToRadioService(context);

        if (getView() != null) {
            try {
                getView().setListStation(dataBase.getStations());
            } catch (NoStationsException e) {
                e.printStackTrace();
            }
        }
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
