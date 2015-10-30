package com.example.rloftus88.wavewatcher.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class ForecastContract {

    private static final String LOG_TAG = ForecastContract.class.getSimpleName();

    //The name for the entire content provider.  A convenient string to use is the package name for
    // the app, which is guaranteed to be unique on the device.
    public static final String CONTENT_AUTHORITY = "com.example.rloftus88.wavewatcher";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's) For instance,
    // content://com.example.rloftus88.wavewatcher.app/weather/ is a valid path for looking at weather data.
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";


    //Inner class that defines the table contents of the location table
    public static final class LocationEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        public static final String COLUMN_LOCATION_SETTING = "spot_id";
        // stores city name as human readable string
        public static final String COLUMN_CITY_NAME = "city_name";

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    //Inner class that defines the columns of the weather table
    public static final class ForecastEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;


        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";


        // Date, stored as long in milliseconds since the epoch
        public static final String COLUMN_LOCAL_TIMESTAMP = "local_timestamp";
        public static final String COLUMN_DAY_NAME = "day_name";
        // Rating, stored as integers (no. of stars)
        public static final String COLUMN_FADED_RATING = "faded_rating";
        public static final String COLUMN_SOLID_RATING = "solid_rating";


        // Min and max break heights for the forecast (stored as floats)
        public static final String COLUMN_SWELL_MIN_BREAK = "min_break";
        public static final String COLUMN_SWELL_MAX_BREAK = "max_break";
        // Human-readable string for swell units
        public static final String COLUMN_SWELL_UNITS = "swell_units";


        // Height of primary swell component (stored as float)
        public static final String COLUMN_SWELL_COMPONENTS_PRIMARY_HEIGHT
                = "swell_primary_height";
        // Period of primary swell component (stored as integer)
        public static final String COLUMN_SWELL_COMPONENTS_PRIMARY_PERIOD
                = "swell_primary_period";
        // Direction of primary swell component in degrees (stored as float)
        public static final String COLUMN_SWELL_COMPONENTS_PRIMARY_DIRECTION
                = "swell_primary_direction";
        // Compass direction of primary swell component (stored as string, eg "WNW" "NNE")
        public static final String COLUMN_SWELL_COMPONENTS_PRIMARY_COMPASS_DIRECTION
                = "swell_primary_compass_direction";


        // Speed of wind (stored as a float)
        public static final String COLUMN_WIND_SPEED = "wind_speed";
        // Direction of wind in degrees (stored as float)
        public static final String COLUMN_WIND_DIRECTION = "wind_direction";
        // Compass direction of wind (stored as string, eg "WNW" "NNE")
        public static final String COLUMN_WIND_COMPASS_DIRECTION = "wind_compass_direction";
        // Air temperature with wind chill taken into account (stored as integer)
        public static final String COLUMN_WIND_CHILL = "wind_chill";
        // Speed of wind gusts (stored as integer)
        public static final String COLUMN_WIND_GUSTS = "wind_gusts";
        // Human-readable string for wind speed units
        public static final String COLUMN_WIND_UNITS = "wind_units";


        // Air pressure (stored as integer)
        public static final String COLUMN_CONDITION_PRESSURE = "condition_pressure";
        // Air temperature (stored as integer)
        public static final String COLUMN_CONDITION_TEMPERATURE = "condition_temperature";
        // Key for weather icon (stored as integer)
        public static final String COLUMN_CONDITION_WEATHER = "condition_weather";
        // Human-readable string for air pressure units
        public static final String COLUMN_CONDITION_UNIT_PRESSURE = "condition_units_pressure";
        // Human-readable string for air temperature units
        public static final String COLUMN_CONDITION_UNITS = "condition_units";


        // URLS for charts displaying various bits of info
        public static final String COLUMN_CHARTS_SWELL = "chart_swell";
        public static final String COLUMN_CHARTS_PERIOD = "chart_period";
        public static final String COLUMN_CHARTS_WIND = "chart_wind";
        public static final String COLUMN_CHARTS_PRESSURE = "chart_pressure";


        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // returns all weather for given location
        public static Uri buildWeatherLocation(String locationSetting) {
            Uri uri = CONTENT_URI.buildUpon().appendPath(locationSetting).build();
//            Log.d(LOG_TAG,"Uri (withLocation) : "+uri.toString());
            return uri;
        }

        // returns all weather for location after the given date
        public static Uri buildWeatherLocationWithStartTime(String locationSetting, long timestamp) {
            Uri uri = CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_LOCAL_TIMESTAMP, Long.toString(timestamp)).build();
//            Log.d(LOG_TAG,"Uri (withStartDate): "+uri.toString());
            return uri;
        }

        // returns a single row
        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            Uri uri = CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(date)).build();
//            Log.d(LOG_TAG,"Uri (withDate) : "+uri.toString());
            return uri;
        }

        // returns all weather for a location on the given day after specified time (hopefully!)
        public static Uri buildWeatherLocationWithDayAndStartTime(String locationSetting, String day, long timestamp) {
            Uri uri = CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(day)
                    .appendQueryParameter(COLUMN_LOCAL_TIMESTAMP, Long.toString(timestamp)).build();
//            Log.d(LOG_TAG,"Uri (withDayAndStart) : "+uri.toString());
            return uri;
        }

        // returns all weather for a location on the given day
        public static Uri buildWeatherLocationWithDay(String locationSetting, String day) {
            Uri uri = CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(day).build();
//            Log.d(LOG_TAG,"Uri (withDay) : "+uri.toString());
            return uri;
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static String getDayNameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_LOCAL_TIMESTAMP);
            if (null != dateString && dateString.length() > 0) {
                return Long.parseLong(dateString);
            } else {
                return 0;
            }
        }

    }
}
