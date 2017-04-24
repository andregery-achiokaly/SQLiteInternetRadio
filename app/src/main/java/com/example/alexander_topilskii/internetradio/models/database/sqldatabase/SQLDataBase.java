package com.example.alexander_topilskii.internetradio.models.database.sqldatabase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.alexander_topilskii.internetradio.models.database.NoStationsException;
import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.DataBase;

import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLDataBaseHelper.STATION_KEY_ID;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLDataBaseHelper.STATION_KEY_IS_CURRENT;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLDataBaseHelper.STATION_KEY_NAME;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLDataBaseHelper.STATION_KEY_SOURCE;
import static com.example.alexander_topilskii.internetradio.models.database.sqldatabase.SQLDataBaseHelper.TABLE_STATIONS;

public class SQLDataBase implements DataBase {
    SQLiteDatabase db;

    public SQLDataBase(SQLiteDatabase db) {
        this.db = db;
    }

    @Override
    public Cursor getStations() throws NoStationsException {
        Cursor cursor = db.query(TABLE_STATIONS, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            return cursor;
        } else {
            throw new NoStationsException("Cursor is null");
        }
    }

    @Override
    public void changeCurrentStations(int id) {
        db.execSQL("UPDATE " + TABLE_STATIONS + " SET " + STATION_KEY_IS_CURRENT + " = 0 WHERE " + STATION_KEY_IS_CURRENT + " = 1");
        db.execSQL("UPDATE " + TABLE_STATIONS + " SET " + STATION_KEY_IS_CURRENT + " = 1 WHERE " + STATION_KEY_ID + " = " + id);
    }

    @Override
    public void deleteStation(int id) {
        db.delete(TABLE_STATIONS, STATION_KEY_ID + " = " + id, null);
    }

    @Override
    public void addStation(String name, String source) {
        ContentValues cv = new ContentValues();
        cv.put(STATION_KEY_IS_CURRENT, 0);
        cv.put(STATION_KEY_NAME, name);
        cv.put(STATION_KEY_SOURCE, source);
        db.insert(TABLE_STATIONS, null, cv);
    }

    @Override
    public Station getCurrentStation() throws NoStationsException {
        Cursor cursor = db.query(TABLE_STATIONS, null, null, null, null, null, null);

        if (cursor != null) {
            boolean isEmpty = !cursor.moveToFirst();
            if (isEmpty) throw new NoStationsException("Station list is empty!");

            int nameIndex = cursor.getColumnIndex(STATION_KEY_NAME);
            int ageIndex = cursor.getColumnIndex(STATION_KEY_SOURCE);
            int idIndex = cursor.getColumnIndex(STATION_KEY_ID);
            int isCurrentIndex = cursor.getColumnIndex(STATION_KEY_IS_CURRENT);

            do {
                if (cursor.getInt(isCurrentIndex) == 1) {
                    Station station = new Station(cursor.getInt(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(ageIndex),
                            true);
                    cursor.close();
                    return station;
                }
            } while (cursor.moveToNext());
            throw new NoStationsException("No current station Exception");
        }
        throw new NoStationsException("Cursor is null");
    }

    @Override
    public Station getStation(int id) throws NoStationsException {
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_STATIONS + " WHERE " + STATION_KEY_ID + "=" + id, null);
        if (cursor != null) {
            cursor.moveToFirst();
            boolean isEmpty = !cursor.moveToFirst();
            if (isEmpty) throw new NoStationsException("Station list is empty!");

            int nameIndex = cursor.getColumnIndex(STATION_KEY_NAME);
            int ageIndex = cursor.getColumnIndex(STATION_KEY_SOURCE);
            int idIndex = cursor.getColumnIndex(STATION_KEY_ID);
            int isCurrentIndex = cursor.getColumnIndex(STATION_KEY_IS_CURRENT);

            Station station = new Station(cursor.getInt(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(ageIndex),
                    cursor.getInt(isCurrentIndex) == 1);
            cursor.close();
            return station;
        } else {
            throw new NoStationsException("No founded station");
        }
    }

    @Override
    public void editStation(int id, String name, String source) {
        ContentValues cv = new ContentValues();
        cv.put(STATION_KEY_NAME, name);
        cv.put(STATION_KEY_SOURCE, source);
        db.update(TABLE_STATIONS, cv, STATION_KEY_ID + " = ?", new String[]{id + ""});
    }

    @Override
    public void closeDataBase() {
        db.close();
    }
}
