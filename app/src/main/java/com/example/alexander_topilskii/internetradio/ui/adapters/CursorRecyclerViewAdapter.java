package com.example.alexander_topilskii.internetradio.ui.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;

import com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLDataBaseHelper;

import java.util.IllegalFormatException;

abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    private Cursor cursor;
    private boolean dataValid;
    private int rowIdColumn;

    CursorRecyclerViewAdapter(Cursor cursor) {
        this.cursor = cursor;
        dataValid = cursor != null;
        rowIdColumn = dataValid ? this.cursor.getColumnIndex(SQLDataBaseHelper.STATION_KEY_ID) : -1;
    }

    @Override
    public int getItemCount() {
        if (dataValid && cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (dataValid && cursor != null && cursor.moveToPosition(position)) {
            return cursor.getLong(rowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!dataValid || !cursor.moveToPosition(position)) {
            throw new IllegalStateException("Cursor date aren't valid");
        }
        onBindViewHolder(viewHolder, cursor);
    }
}
