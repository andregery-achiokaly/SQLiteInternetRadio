package com.example.alexander_topilskii.internetradio.models.database.sqldatabase;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLDataBaseHelper extends SQLiteOpenHelper{
    static final String TABLE_STATIONS = "TABLE_STATIONS";
    public static final String STATION_KEY_ID = "_id";
    public static final String STATION_KEY_IS_CURRENT = "KEY_IS_CURRENT";
    public static final String STATION_KEY_NAME = "KEY_NAME";
    public static final String STATION_KEY_SOURCE = "KEY_SOURCE";

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = SQLDataBaseHelper.class.getSimpleName();

    public SQLDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
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
        cv.put(STATION_KEY_IS_CURRENT, 1);
        cv.put(STATION_KEY_NAME, "MusicRadio");
        cv.put(STATION_KEY_SOURCE, "http://musicradio.ipfm.net:80/MusicRadio");
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
}
