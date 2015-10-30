package com.example.rloftus88.wavewatcher;


import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.example.rloftus88.wavewatcher.data.ForecastContract;

public class TestFetchForecastTask extends AndroidTestCase {

    static final String ADD_LOCATION_SETTING = "52";
    static final String ADD_LOCATION_CITY_NAME = "Lahinch - Beach";

    /*
        Students: uncomment testAddLocation after you have written the AddLocation function.
        This test will only run on API level 11 and higher because of a requirement in the
        content provider.
     */
    @TargetApi(11)
    public void testAddLocation() {
        // start from a clean state
        getContext().getContentResolver().delete(ForecastContract.LocationEntry.CONTENT_URI,
                ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{ADD_LOCATION_SETTING});

        FetchForecastTask fft = new FetchForecastTask(getContext());
        long locationId = fft.addLocation(ADD_LOCATION_SETTING, ADD_LOCATION_CITY_NAME);

        // does addLocation return a valid record ID?
        assertFalse("Error: addLocation returned an invalid ID on insert",
                locationId == -1);

        // test all this twice
        for ( int i = 0; i < 2; i++ ) {

            // does the ID point to our location?
            Cursor locationCursor = getContext().getContentResolver().query(
                    ForecastContract.LocationEntry.CONTENT_URI,
                    new String[]{
                            ForecastContract.LocationEntry._ID,
                            ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING,
                            ForecastContract.LocationEntry.COLUMN_CITY_NAME
                    },
                    ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                    new String[]{ADD_LOCATION_SETTING},
                    null);

            // these match the indices of the projection
            if (locationCursor.moveToFirst()) {
                assertEquals("Error: the queried value of locationId does not match the returned value" +
                        "from addLocation", locationCursor.getLong(0), locationId);
                assertEquals("Error: the queried value of location setting is incorrect",
                        locationCursor.getString(1), ADD_LOCATION_SETTING);
                assertEquals("Error: the queried value of location city is incorrect",
                        locationCursor.getString(2), ADD_LOCATION_CITY_NAME);
            } else {
                fail("Error: the id you used to query returned an empty cursor");
            }

            // there should be no more records
            assertFalse("Error: there should be only one record returned from a location query",
                    locationCursor.moveToNext());

            // add the location again
            long newLocationId = fft.addLocation(ADD_LOCATION_SETTING, ADD_LOCATION_CITY_NAME);

            assertEquals("Error: inserting a location again should return the same ID",
                    locationId, newLocationId);

            locationCursor.close();
        }
        // reset our state back to normal
        getContext().getContentResolver().delete(ForecastContract.LocationEntry.CONTENT_URI,
                ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ?",
                new String[]{ADD_LOCATION_SETTING});

        // clean up the test so that other tests can use the content provider
        getContext().getContentResolver().
                acquireContentProviderClient(ForecastContract.LocationEntry.CONTENT_URI).
                getLocalContentProvider().shutdown();

    }
}
