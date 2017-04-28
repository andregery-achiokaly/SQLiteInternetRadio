package com.example.alexander_topilskii.internetradio.ui.activitys;

import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.alexander_topilskii.internetradio.R;
import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.player.PlayerService;
import com.example.alexander_topilskii.internetradio.models.player.State;
import com.example.alexander_topilskii.internetradio.presenters.BasePresenter;
import com.example.alexander_topilskii.internetradio.ui.adapters.StationsListCursorAdapter;
import com.example.alexander_topilskii.internetradio.ui.dialog.EditStationDialog;
import com.example.alexander_topilskii.internetradio.ui.interfaces.AdapterCallback;
import com.example.alexander_topilskii.internetradio.ui.interfaces.BaseActivity;
import com.example.alexander_topilskii.internetradio.ui.views.WaveView;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.alexander_topilskii.internetradio.presenters.BasePresenter.TAG_EDIT_STATION_DIALOG;

public class MainActivity extends MvpActivity<BaseActivity, BasePresenter> implements BaseActivity, AdapterCallback {
    public static final int WAVE_VIEW_RECORD_AUDIO_PERMISSION = 4115;
    @BindView(R.id.wave_view)
    WaveView waveView;
    @BindView(R.id.recycler_stations_list)
    RecyclerView recyclerView;
    @BindView(R.id.play_button)
    FloatingActionButton playButton;
    StationsListCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initComponents();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    private void initComponents() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        playButton.setOnClickListener(v -> getPresenter().playButtonClick());
    }

    @NonNull
    @Override
    public BasePresenter createPresenter() {
        return new BasePresenter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPresenter().onPause(this);
    }

    @Override
    public void setListStation(Cursor stations) {
        runOnUiThread(() -> {
            adapter = new StationsListCursorAdapter(this, stations);
            recyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void setAudioWave(byte[] bytes) {
        if (waveView != null) waveView.updateVisualizer(bytes);
    }

    @Override
    public void changeState(State state) {
        playButton.setImageResource(state.getResourceBitmap());
    }

    @Override
    public void changeState(State state, String message) {
        playButton.setImageResource(state.getResourceBitmap());
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDialog(int id, String name, String source) {
        EditStationDialog editStationDialog = EditStationDialog.newInstance(id, name, source);
        editStationDialog.setOnChangeDialogResultListener((id1, name1, source1) -> getPresenter().editStation(id1, name1, source1));
        editStationDialog.show(this.getSupportFragmentManager(), TAG_EDIT_STATION_DIALOG);
    }

    @Override
    public void shareRadio(String name, String source) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "It is my favorite Radio: " + name + "\n" + source);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    @Override
    public void itemClick(Station station) {
        getPresenter().stationClick(station);
    }

    @Override
    public void itemLongClick(Station station) {
        getPresenter().stationLongClick(this, station);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_station_menu_btn:
                getPresenter().addStationClick(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            stopService(new Intent(this, PlayerService.class));
        }
        return super.onKeyDown(keyCode, event);
    }
}
