package com.example.alexander_topilskii.internetradio.presenters;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;

import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.OnDataBaseChangedListener;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.ResultListener;
import com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLiteDataBaseInteractor;
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
    private Player player;
    private boolean canShow;
    private OnChangeDialogResultListener onDialogResultListener;
    private SQLiteDataBaseInteractor sqLiteDataBaseInteractor;

    public BasePresenter(Context context) {
        radioServiceConnection = new RadioServiceConnection();
        ResultListener resultListener = getResultListener();
        sqLiteDataBaseInteractor = new SQLiteDataBaseInteractor(context,
                ((MainActivity) context).getSupportLoaderManager(),
                resultListener,
                getOnDataBaseChangedListener());

        playerCallbackListener = getPlayerCallbackListener();
        radioVisualizer = new RadioVisualizer();
        onDialogResultListener = getOnDialogResultListener(((MainActivity) context).getSupportFragmentManager());
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
    private OnDataBaseChangedListener getOnDataBaseChangedListener() {
        return () -> sqLiteDataBaseInteractor.getStations();
    }

    @NonNull
    private OnChangeDialogResultListener getOnDialogResultListener(final FragmentManager fragmentManager) {
        return new OnChangeDialogResultListener() {
            @Override
            public void onShareResult(int id) {

            }

            @Override
            public void onDeleteResult(int id) {
                sqLiteDataBaseInteractor.deleteStation(id);
            }

            @Override
            public void onEditResult(int id, String name, String source) {
                EditStationDialog editStationDialog = EditStationDialog.newInstance(id, name, source);
                editStationDialog.setOnChangeDialogResultListener((id1, name1, source1) -> sqLiteDataBaseInteractor.editStation(id1, name1, source1));
                editStationDialog.show(fragmentManager, TAG_EDIT_STATION_DIALOG);
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
        sqLiteDataBaseInteractor.changeCurrentStations(station.getId());
        if (player != null) {
            if (currentStation == station) player.changeState(currentStation);
            else player.setNewStation(station);
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
        if (player != null) player.changeState(currentStation);
    }

    @Override
    public void onResume(Context context) {
        sqLiteDataBaseInteractor.getCurrentStation();
        bindToRadioService(context);
        sqLiteDataBaseInteractor.getStations();
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
        changeStationDialog.setOnAddDialogResultListener((name, source) -> sqLiteDataBaseInteractor.addStation(name, source));
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
