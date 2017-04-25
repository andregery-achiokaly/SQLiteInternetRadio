package com.example.alexander_topilskii.internetradio.presenters.interfaces;


import android.content.Context;

import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.ui.activitys.MainActivity;

public interface BasePresenterInterface {
    void stationClick(Station station) ;

    void stationLongClick(MainActivity context, Station station);

    void playButtonClick();

    void onResume(Context context);

    void onPause(Context context);

    void setCanShowVizualizer(boolean canShow);

    void addStationClick(MainActivity activity);
}
