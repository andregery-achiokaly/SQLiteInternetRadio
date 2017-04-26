package com.example.alexander_topilskii.internetradio.models.database.sqldatabase;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.alexander_topilskii.internetradio.models.database.Station;
import com.example.alexander_topilskii.internetradio.models.database.interfaces.DataBase;

public class SQLDataBaseHelper extends SQLiteOpenHelper implements DataBase {
    private static final String TABLE_STATIONS = "TABLE_STATIONS";
    public static final String STATION_KEY_ID = "_id";
    public static final String STATION_KEY_IS_CURRENT = "KEY_IS_CURRENT";
    public static final String STATION_KEY_NAME = "KEY_NAME";
    public static final String STATION_KEY_SOURCE = "KEY_SOURCE";

    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = SQLDataBaseHelper.class.getSimpleName();
    private SQLiteDatabase db;

    public SQLDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_STATIONS +
                "(" +
                STATION_KEY_ID + " integer primary key, " +
                STATION_KEY_NAME + " text, " +
                STATION_KEY_SOURCE + " text, " +
                STATION_KEY_IS_CURRENT + " numeric(1)" +
                ")");

        addFirstStation(db);
    }

    private void addFirstStation(SQLiteDatabase db) {
        ContentValues cv = new ContentValues();
        cv.put(STATION_KEY_NAME, "MusicRadio");
        cv.put(STATION_KEY_SOURCE, "http://musicradio.ipfm.net:80/MusicRadio");
        db.insert(TABLE_STATIONS, null, cv);

        cv = new ContentValues();
        cv.put(STATION_KEY_NAME, "Ретро фм");
        cv.put(STATION_KEY_SOURCE, "http://cast.radiogroup.com.ua:8000/retro");
        db.insert(TABLE_STATIONS, null, cv);

        cv = new ContentValues();
        cv.put(STATION_KEY_NAME, "Перец фм");
        cv.put(STATION_KEY_SOURCE, "http://urg.adamant.net:8080/radio-stilnoe");
        db.insert(TABLE_STATIONS, null, cv);

        cv = new ContentValues();
        cv.put(STATION_KEY_NAME, "Радио Мелодия");
        cv.put(STATION_KEY_SOURCE, "http://radio.melodiya.ua:8080/radio-melodia");
        db.insert(TABLE_STATIONS, null, cv);

        cv = new ContentValues();
        cv.put(STATION_KEY_NAME, "Jam FM");
        cv.put(STATION_KEY_SOURCE, "http://online-radioroks.tavrmedia.ua/RadioROKS");
        db.insert(TABLE_STATIONS, null, cv);

        cv = new ContentValues();
        cv.put(STATION_KEY_NAME, "Русское Радио");
        cv.put(STATION_KEY_SOURCE, "http://online-rusradio.tavrmedia.ua/RusRadio");
        db.insert(TABLE_STATIONS, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ContentValues cv = new ContentValues();
        cv.put(STATION_KEY_IS_CURRENT, 0);
        cv.put(STATION_KEY_NAME, "Jamfm");
        cv.put(STATION_KEY_SOURCE, "http://cast.radiogroup.com.ua:8000/jamfm");
        db.insert(TABLE_STATIONS, null, cv);
    }

    @Override
    public Cursor getStations() {
        Cursor cursor = db.query(TABLE_STATIONS, null, null, null, null, null, null);
        if (cursor != null) {
            return cursor;
        } else {
            return null;
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
    public Station getCurrentStation() {
        Cursor cursor = db.query(TABLE_STATIONS, null, null, null, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();

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
        }
        return null;
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
