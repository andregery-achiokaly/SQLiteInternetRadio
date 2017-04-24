package com.example.alexander_topilskii.internetradio.ui.adapters;


import android.database.Cursor;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexander_topilskii.internetradio.R;
import com.example.alexander_topilskii.internetradio.models.database.NoStationsException;
import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.ui.interfaces.AdapterCallback;

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

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView source;
        Station station;

        ViewHolder(View view) {
            super(view);
            name = (TextView) itemView.findViewById(R.id.name_field);
            source = (TextView) itemView.findViewById(R.id.source_field);
            view.setOnClickListener(v -> {
                try {
                    callback.itemClick(station);
                } catch (NoStationsException e) {
                    e.printStackTrace();
                }
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
            this.station = station;
            if (station.isCurrent()) {
                this.name.setTextColor(Color.BLUE);
                this.source.setTextColor(Color.BLUE);
            }
        }
    }
}
