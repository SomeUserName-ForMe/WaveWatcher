package com.example.rloftus88.wavewatcher.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.rloftus88.wavewatcher.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your ForecastContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {

    static final String TEST_LOCATION = "52"; // Lachinch - Beach
    static final long TEST_DATE = 1435017600L*1000L;  // Tue, 23 Jun 2015 17:09:33

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(idx);
            assertEquals("Expected value '" + expectedValue +
                            "' did not match the actual value '" + actualValue + "'. " + error,
                    expectedValue, actualValue);
        }
    }

    /*
        Students: Use this to create some default weather values for your database tests.
     */
    static ContentValues createWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_LOCAL_TIMESTAMP, TEST_DATE);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_FADED_RATING, 0);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_SOLID_RATING, 1);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_SWELL_MIN_BREAK, 0.8);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_SWELL_MAX_BREAK, 1.3);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_SWELL_UNITS, "m");
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_HEIGHT, 1.1);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_PERIOD, 9);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_DIRECTION, 96.65);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_COMPASS_DIRECTION, "W");
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_WIND_SPEED, 7);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_WIND_DIRECTION, 184);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_WIND_COMPASS_DIRECTION, "N");
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_WIND_CHILL, 12);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_WIND_GUSTS, 8);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_WIND_UNITS, "kph");
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_CONDITION_PRESSURE, 1018);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_CONDITION_TEMPERATURE, 13);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_CONDITION_WEATHER, 10);
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_CONDITION_UNIT_PRESSURE, "mb");
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_CONDITION_UNITS, "c");
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_CHARTS_SWELL, "http://hist-1.msw.ms/wave/750/1-1435017600-1.gif");
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_CHARTS_PERIOD, "http://hist-1.msw.ms/wave/750/1-1435017600-2.gif");
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_CHARTS_WIND, "http://hist-1.msw.ms/gfs/750/1-1435017600-4.gif");
        weatherValues.put(ForecastContract.ForecastEntry.COLUMN_CHARTS_PRESSURE, "http://hist-1.msw.ms/gfs/750/1-1435017600-3.gif");

        return weatherValues;
    }

    /*
        Students: You can uncomment this helper function once you have finished creating the
        LocationEntry part of the ForecastContract.
     */
    static ContentValues createLahinchLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        testValues.put(ForecastContract.LocationEntry.COLUMN_CITY_NAME, "Lahinch - Beach");

        return testValues;
    }

    /*
        Students: You can uncomment this function once you have finished creating the
        LocationEntry part of the ForecastContract as well as the ForecastDbHelper.
     */
    static long insertLahinchLocationValues(Context context) {
        // insert our test records into the database
        ForecastDbHelper dbHelper = new ForecastDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createLahinchLocationValues();

        long locationRowId;
        locationRowId = db.insert(ForecastContract.LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Lahinch Location Values", locationRowId != -1);

        return locationRowId;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.

        Note that this only tests that the onChange function is called; it does not test that the
        correct Uri is returned.
     */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
