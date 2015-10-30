package com.example.rloftus88.wavewatcher.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rloftus88.wavewatcher.data.ForecastContract.ForecastEntry;
import com.example.rloftus88.wavewatcher.data.ForecastContract.LocationEntry;

public class ForecastDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "forecasts.db";

    public ForecastDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE "+LocationEntry.TABLE_NAME +" ("

                +LocationEntry._ID+" INTEGER PRIMARY KEY, "
                +LocationEntry.COLUMN_LOCATION_SETTING+" TEXT UNIQUE NOT NULL, "
                +LocationEntry.COLUMN_CITY_NAME+" TEXT NOT NULL "
                +");";

        final String SQL_CREATE_FORECAST_TABLE = "CREATE TABLE "+ForecastEntry.TABLE_NAME+" ( "

                +ForecastEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                +ForecastEntry.COLUMN_LOC_KEY+" INTEGER NOT NULL, "
                +ForecastEntry.COLUMN_LOCAL_TIMESTAMP+" INTEGER NOT NULL, "
                +ForecastEntry.COLUMN_DAY_NAME+" TEXT NOT NULL, "
                +ForecastEntry.COLUMN_FADED_RATING+" INTEGER NOT NULL, "
                +ForecastEntry.COLUMN_SOLID_RATING+" INTEGER NOT NULL, "

                +ForecastEntry.COLUMN_SWELL_MIN_BREAK+" REAL NOT NULL, "
                +ForecastEntry.COLUMN_SWELL_MAX_BREAK+" REAL NOT NULL, "
                +ForecastEntry.COLUMN_SWELL_UNITS+" TEXT NOT NULL, "

                +ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_HEIGHT+" REAL NOT NULL, "
                +ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_PERIOD+" INTEGER NOT NULL, "
                +ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_DIRECTION+" REAL NOT NULL, "
                +ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_COMPASS_DIRECTION+" TEXT NOT NULL, "

                +ForecastEntry.COLUMN_WIND_SPEED+" INTEGER NOT NULL, "
                +ForecastEntry.COLUMN_WIND_DIRECTION+" INTEGER NOT NULL, "
                +ForecastEntry.COLUMN_WIND_COMPASS_DIRECTION+" TEXT NOT NULL, "
                +ForecastEntry.COLUMN_WIND_CHILL+" INTEGER NOT NULL, "
                +ForecastEntry.COLUMN_WIND_GUSTS+" INTEGER NOT NULL, "
                +ForecastEntry.COLUMN_WIND_UNITS+" TEXT NOT NULL, "

                +ForecastEntry.COLUMN_CONDITION_PRESSURE+" INTEGER NOT NULL, "
                +ForecastEntry.COLUMN_CONDITION_TEMPERATURE+" INTEGER NOT NULL, "
                +ForecastEntry.COLUMN_CONDITION_WEATHER+" INTEGER NOT NULL, "
                +ForecastEntry.COLUMN_CONDITION_UNIT_PRESSURE+" TEXT NOT NULL, "
                +ForecastEntry.COLUMN_CONDITION_UNITS+" TEXT NOT NULL, "

                +ForecastEntry.COLUMN_CHARTS_SWELL+" TEXT NOT NULL, "
                +ForecastEntry.COLUMN_CHARTS_PERIOD+" TEXT NOT NULL, "
                +ForecastEntry.COLUMN_CHARTS_WIND+" TEXT NOT NULL, "
                +ForecastEntry.COLUMN_CHARTS_PRESSURE+" TEXT NOT NULL, "

                +"FOREIGN KEY ( "+ForecastEntry.COLUMN_LOC_KEY+" ) REFERENCES "
                +LocationEntry.TABLE_NAME+" ( "+LocationEntry._ID+" ), "


                +"UNIQUE ( "+ForecastEntry.COLUMN_LOCAL_TIMESTAMP+" , "
                +ForecastEntry.COLUMN_LOC_KEY+" ) ON CONFLICT REPLACE "
                +");";

        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_FORECAST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ForecastEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
