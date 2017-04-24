package com.example.alexander_topilskii.internetradio.ui.activitys;

import android.database.Cursor;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.example.alexander_topilskii.internetradio.R;
import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.player.State;
import com.example.alexander_topilskii.internetradio.presenters.BasePresenter;
import com.example.alexander_topilskii.internetradio.ui.adapters.StationsListCursorAdapter;
import com.example.alexander_topilskii.internetradio.ui.interfaces.AdapterCallback;
import com.example.alexander_topilskii.internetradio.ui.interfaces.BaseActivity;
import com.example.alexander_topilskii.internetradio.ui.views.WaveView;
import com.hannesdorfmann.mosby.mvp.MvpActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends MvpActivity<BaseActivity, BasePresenter> implements BaseActivity, AdapterCallback {
    public static final int WAVE_VIEW_RECORD_AUDIO_PERMISSION = 4115;
    @BindView(R.id.wave_view)
    WaveView waveView;
    @BindView(R.id.recycler_stations_list)
    RecyclerView recyclerView;
    @BindView(R.id.play_button)
    FloatingActionButton playButton;

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
        playButton.setOnClickListener(v -> presenter.playButtonClick());
    }

    @NonNull
    @Override
    public BasePresenter createPresenter() {
        return new BasePresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause(this);
    }

    @Override
    public void setListStation(Cursor stations) {
        recyclerView.setAdapter(new StationsListCursorAdapter(this, stations));
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
    public void itemClick(Station station) {
        presenter.stationClick(station);
    }

    @Override
    public void itemLongClick(Station station) {
        presenter.stationLongClick(this, station);
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
                presenter.addStationClick(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
