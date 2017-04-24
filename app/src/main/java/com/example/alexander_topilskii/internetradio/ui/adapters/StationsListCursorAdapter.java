package com.example.alexander_topilskii.internetradio.ui.adapters;


import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexander_topilskii.internetradio.R;
import com.example.alexander_topilskii.internetradio.models.database.NoStationsException;
import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.ui.interfaces.AdapterCallback;
import com.example.alexander_topilskii.internetradio.ui.interfaces.StationListAdapterInterface;

public class StationsListCursorAdapter extends CursorRecyclerViewAdapter<StationsListCursorAdapter.ViewHolder> {
    private AdapterCallback callback;

    public StationsListCursorAdapter(AdapterCallback callback, Cursor cursor) {
        super(cursor);
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.station_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        try {
            Station station = StationItem.fromCursor(cursor);

            viewHolder.setName(station.getName());
            viewHolder.setSource(station.getSource());
            viewHolder.setStation(station);
        } catch (NoStationsException e) {
            e.printStackTrace();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder implements StationListAdapterInterface {
        private final TextView name;
        private final TextView source;
        Station station;

        ViewHolder(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.name_field);
            source = (TextView) itemView.findViewById(R.id.source_field);
            view.setOnClickListener(v -> {
                Log.v("GGGG", station.getSource() + " " + station.getName() + " " + station.isCurrent());
                callback.itemClick(station);
            });
            view.setOnLongClickListener(v -> {
                callback.itemLongClick(station);
                return false;
            });
        }

        void setName(String name) {
            this.name.setText(name);
        }

        void setSource(String source) {
            this.source.setText(source);
        }

        void setStation(Station station) {
//            if (station.isCurrent()) {
//                this.source.setBackgroundColor(232);
//            }
        }

        @Override
        public void setCurrentStation(int id) {

        }
    }
}
