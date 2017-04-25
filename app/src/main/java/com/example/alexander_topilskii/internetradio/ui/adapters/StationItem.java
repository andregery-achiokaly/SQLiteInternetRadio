package com.example.alexander_topilskii.internetradio.ui.adapters;


import android.database.Cursor;

import com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLDataBaseHelper;
import com.example.alexander_topilskii.internetradio.models.database.Station;

class StationItem {
    private String name;
    private String source;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    static Station fromCursor(Cursor cursor) {
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(SQLDataBaseHelper.STATION_KEY_NAME);
            int sourceIndex = cursor.getColumnIndex(SQLDataBaseHelper.STATION_KEY_SOURCE);
            int idIndex = cursor.getColumnIndex(SQLDataBaseHelper.STATION_KEY_ID);
            int isCurrentIndex = cursor.getColumnIndex(SQLDataBaseHelper.STATION_KEY_IS_CURRENT);

            boolean isCurrent = cursor.getInt(isCurrentIndex) == 1;
            return new Station(cursor.getInt(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(sourceIndex),
                    isCurrent);
        }
        return null;
    }
}
