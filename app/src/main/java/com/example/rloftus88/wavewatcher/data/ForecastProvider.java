package com.example.rloftus88.wavewatcher.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;

public class ForecastProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ForecastDbHelper mOpenHelper;

    static final int WEATHER = 100;
    static final int WEATHER_WITH_LOCATION = 101;
    static final int WEATHER_WITH_LOCATION_AND_DAY = 102;
    static final int WEATHER_WITH_LOCATION_AND_TIME = 103;
    static final int WEATHER_WITH_LOCATION_AND_DAY_AND_TIME = 104;
    static final int LOCATION = 300;

    private static final SQLiteQueryBuilder sWeatherByLocationSettingQueryBuilder;

    static{
        sWeatherByLocationSettingQueryBuilder = new SQLiteQueryBuilder();

        //This is an inner join which looks like
        //weather INNER JOIN location ON weather.location_id = location._id
        sWeatherByLocationSettingQueryBuilder.setTables(
                ForecastContract.ForecastEntry.TABLE_NAME + " INNER JOIN " +
                        ForecastContract.LocationEntry.TABLE_NAME +
                        " ON " + ForecastContract.ForecastEntry.TABLE_NAME +
                        "." + ForecastContract.ForecastEntry.COLUMN_LOC_KEY +
                        " = " + ForecastContract.LocationEntry.TABLE_NAME +
                        "." + ForecastContract.LocationEntry._ID);
    }

    //location.location_setting = ?
    private static final String sLocationSettingSelection =
            ForecastContract.LocationEntry.TABLE_NAME+
                    "." + ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? ";

    //location.location_setting = ? AND date >= ?
    private static final String sLocationSettingWithStartDateSelection =
            ForecastContract.LocationEntry.TABLE_NAME+
                    "." + ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    ForecastContract.ForecastEntry.COLUMN_LOCAL_TIMESTAMP + " >= ? ";

    //location.location_setting = ? AND local_timestamp >= ? AND day_name = ?
    private static final String sLocationSettingWithDayAndStartTimeSelection =
            ForecastContract.LocationEntry.TABLE_NAME+
                    "." + ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    ForecastContract.ForecastEntry.COLUMN_DAY_NAME + " = ? AND " +
                    ForecastContract.ForecastEntry.COLUMN_LOCAL_TIMESTAMP + " > ? ";

    //location.location_setting = ? AND date = ?
    private static final String sLocationSettingAndTimeSelection =
            ForecastContract.LocationEntry.TABLE_NAME +
                    "." + ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    ForecastContract.ForecastEntry.COLUMN_LOCAL_TIMESTAMP + " = ? ";

    //location.location_setting = ? AND date = ?
    private static final String sLocationSettingAndDaySelection =
            ForecastContract.LocationEntry.TABLE_NAME +
                    "." + ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING + " = ? AND " +
                    ForecastContract.ForecastEntry.COLUMN_DAY_NAME + " = ? ";


    private Cursor getWeatherByLocationSetting(Uri uri, String[] projection, String sortOrder) {
        String locationSetting = ForecastContract.ForecastEntry.getLocationSettingFromUri(uri);
        long startDate = ForecastContract.ForecastEntry.getStartDateFromUri(uri);

        String[] selectionArgs;
        String selection;

        if (startDate == 0) {
            selection = sLocationSettingSelection;
            selectionArgs = new String[]{locationSetting};
        } else {
            selectionArgs = new String[]{locationSetting, Long.toString(startDate)};
            selection = sLocationSettingWithStartDateSelection;

            return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                    projection,
                    selection,
                    selectionArgs,
                    ForecastContract.ForecastEntry.COLUMN_DAY_NAME,
                    null,
                    sortOrder
            );

        }

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByLocationSettingAndTime(
            Uri uri, String[] projection, String sortOrder) {
        String locationSetting = ForecastContract.ForecastEntry.getLocationSettingFromUri(uri);
        long date = ForecastContract.ForecastEntry.getDateFromUri(uri);

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndTimeSelection,
                new String[]{locationSetting, Long.toString(date)},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByLocationSettingAndDay(
            Uri uri, String[] projection, String sortOrder) {
        String locationSetting = ForecastContract.ForecastEntry.getLocationSettingFromUri(uri);
        String day = ForecastContract.ForecastEntry.getDayNameFromUri(uri);

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingAndDaySelection,
                new String[]{locationSetting, day},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getWeatherByLocationSettingDayAndStartTime(
            Uri uri, String[] projection, String sortOrder) {
        String locationSetting = ForecastContract.ForecastEntry.getLocationSettingFromUri(uri);
        String day = ForecastContract.ForecastEntry.getDayNameFromUri(uri);
        long timestamp = ForecastContract.ForecastEntry.getDateFromUri(uri);

        return sWeatherByLocationSettingQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                sLocationSettingWithDayAndStartTimeSelection,
                new String[]{locationSetting, day, Long.toString(timestamp)},
                null,
                null,
                sortOrder
        );
    }
    /*
        Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the WEATHER, WEATHER_WITH_LOCATION, WEATHER_WITH_LOCATION_AND_TIME,
        and LOCATION integer constants defined above.  You can test this by uncommenting the
        testUriMatcher test within TestUriMatcher.
     */
    static UriMatcher buildUriMatcher() {
        // 1) The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case. Add the constructor below.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ForecastContract.CONTENT_AUTHORITY;
        // 2) Use the addURI function to match each of the types.
        matcher.addURI(authority, ForecastContract.PATH_WEATHER, WEATHER);
        matcher.addURI(authority, ForecastContract.PATH_WEATHER+"/*", WEATHER_WITH_LOCATION);
        matcher.addURI(authority, ForecastContract.PATH_WEATHER+"/*/*", WEATHER_WITH_LOCATION_AND_DAY);
        matcher.addURI(authority, ForecastContract.PATH_WEATHER+"/*/#",
                WEATHER_WITH_LOCATION_AND_TIME);
        matcher.addURI(authority, ForecastContract.PATH_WEATHER+"/*/*/#",
                WEATHER_WITH_LOCATION_AND_DAY_AND_TIME);
        matcher.addURI(authority, ForecastContract.PATH_LOCATION, LOCATION);
        // 3) Return the new matcher!
        return matcher;
    }

    /*
        Students: We've coded this for you.  We just create a new ForecastDbHelper for later use
        here.
     */
    @Override
    public boolean onCreate() {
        mOpenHelper = new ForecastDbHelper(getContext());
        return true;
    }

    /*
        Students: Here's where you'll code the getType function that uses the UriMatcher.  You can
        test this by uncommenting testGetType in TestProvider.

     */
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case WEATHER_WITH_LOCATION_AND_TIME:
                return ForecastContract.ForecastEntry.CONTENT_ITEM_TYPE;
            case WEATHER_WITH_LOCATION_AND_DAY_AND_TIME:
                return ForecastContract.ForecastEntry.CONTENT_TYPE;
            case WEATHER_WITH_LOCATION_AND_DAY:
                return ForecastContract.ForecastEntry.CONTENT_TYPE;
            case WEATHER_WITH_LOCATION:
                return ForecastContract.ForecastEntry.CONTENT_TYPE;
            case WEATHER:
                return ForecastContract.ForecastEntry.CONTENT_TYPE;
            case LOCATION:
                return ForecastContract.LocationEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "weather/*/*/#"
            case WEATHER_WITH_LOCATION_AND_DAY_AND_TIME:
            {
                retCursor = getWeatherByLocationSettingDayAndStartTime(uri, projection, sortOrder);
                break;
            }
            // "weather/*/#"
            case WEATHER_WITH_LOCATION_AND_TIME:
            {
                retCursor = getWeatherByLocationSettingAndTime(uri, projection, sortOrder);
                break;
            }
            // "weather/*/*"
            case WEATHER_WITH_LOCATION_AND_DAY: {
                retCursor = getWeatherByLocationSettingAndDay(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case WEATHER_WITH_LOCATION: {
                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);
                break;
            }
            // "weather"
            case WEATHER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ForecastContract.ForecastEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "location"
            case LOCATION: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        ForecastContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case WEATHER: {
                long _id = db.insert(ForecastContract.ForecastEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ForecastContract.ForecastEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOCATION: {
                long _id = db.insert(ForecastContract.LocationEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ForecastContract.LocationEntry.buildLocationUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (null == selection)  selection = "1";
        switch (match) {
            case WEATHER: {
                rowsDeleted = db.delete(ForecastContract.ForecastEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            case LOCATION: {
                rowsDeleted = db.delete(ForecastContract.LocationEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (0 != rowsDeleted) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case WEATHER: {
                rowsUpdated = db.update(ForecastContract.ForecastEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case LOCATION: {
                rowsUpdated = db.update(ForecastContract.LocationEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (0 != rowsUpdated) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case WEATHER: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ForecastContract.ForecastEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }
            default: {
                return super.bulkInsert(uri, values);
            }
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
