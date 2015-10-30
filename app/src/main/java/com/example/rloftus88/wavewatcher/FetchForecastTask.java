package com.example.rloftus88.wavewatcher;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.rloftus88.wavewatcher.data.ApiKey;
import com.example.rloftus88.wavewatcher.data.ForecastContract;
import com.example.rloftus88.wavewatcher.data.ForecastContract.ForecastEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Vector;


public class FetchForecastTask extends AsyncTask<String, Void, Void> {

    private static final String LOG_TAG = FetchForecastTask.class.getSimpleName();

    private final Context mContext;

    public FetchForecastTask (Context context) {
        mContext = context;
    }


    long addLocation(String spotId, String cityName) {
        long locationID;
        // Check if the location with this city name exists in the db
        Cursor locationCursor = mContext.getContentResolver().query(
                ForecastContract.LocationEntry.CONTENT_URI,
                new String[]{ForecastContract.LocationEntry._ID},
                ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING+" = ?",
                new String[]{spotId},
                null
        );

        // If it exists, return the current ID
        if (locationCursor.moveToFirst()) {
            int locationIDIndex = locationCursor.getColumnIndex(ForecastContract.LocationEntry._ID);
            locationID = locationCursor.getLong(locationIDIndex);
        } else {
        // Otherwise, insert it using the content resolver and the base URI
            ContentValues locationValues = new ContentValues();
            locationValues.put(ForecastContract.LocationEntry.COLUMN_LOCATION_SETTING, spotId);
            locationValues.put(ForecastContract.LocationEntry.COLUMN_CITY_NAME, cityName);

            Uri insertedUri = mContext.getContentResolver().insert(
                    ForecastContract.LocationEntry.CONTENT_URI, locationValues
            );
            locationID = ContentUris.parseId(insertedUri);
        }
        locationCursor.close();
        return locationID;
    }



    private Void getForecastFromJSON(String forecastJsonStr, String spotID) throws JSONException {

        // names of the fields we will extract (as they appear in the JSON)
        final String MSW_TIME = "localTimestamp";
        final String MSW_FADED_RATING = "fadedRating";
        final String MSW_SOLID_RATING = "solidRating";
        final String MSW_SWELL = "swell";
        final String MSW_SWELL_MIN_BREAK = "minBreakingHeight";
        final String MSW_SWELL_MAX_BREAK = "maxBreakingHeight";
        final String MSW_SWELL_UNITS = "unit";
        final String MSW_SWELL_COMPONENTS = "components";
        final String MSW_SWELL_COMPONENTS_PRIMARY = "primary";
        final String MSW_SWELL_COMPONENTS_PRIMARY_HEIGHT = "height";
        final String MSW_SWELL_COMPONENTS_PRIMARY_PERIOD = "period";
        final String MSW_SWELL_COMPONENTS_PRIMARY_DIRECTION = "direction";
        final String MSW_SWELL_COMPONENTS_PRIMARY_COMPASS_DIRECTION = "compassDirection";
        final String MSW_WIND = "wind";
        final String MSW_WIND_SPEED = "speed";
        final String MSW_WIND_DIRECTION = "direction";
        final String MSW_WIND_COMPASS_DIRECTION = "compassDirection";
        final String MSW_WIND_CHILL = "chill";
        final String MSW_WIND_GUSTS = "gusts";
        final String MSW_WIND_UNITS = "unit";
        final String MSW_CONDITION = "condition";
        final String MSW_CONDITION_PRESSURE = "pressure";
        final String MSW_CONDITION_TEMPERATURE = "temperature";
        final String MSW_CONDITION_WEATHER = "weather";
        final String MSW_CONDITION_UNIT_PRESSURE = "unitPressure";
        final String MSW_CONDITION_UNITS = "unit";
        final String MSW_CHARTS = "charts";
        final String MSW_CHARTS_SWELL = "swell";
        final String MSW_CHARTS_PERIOD = "period";
        final String MSW_CHARTS_WIND = "wind";
        final String MSW_CHARTS_PRESSURE = "pressure";


        try {

            JSONArray forecastJson = new JSONArray(forecastJsonStr);
            Log.d(LOG_TAG, "Size of JSONArray: " + forecastJson.length());

            String locationString = Utility.getReadableLocationFromSpotID(mContext, spotID);
            long locationID = addLocation(spotID, locationString);

            Log.v(LOG_TAG, "spotID: " + spotID + ", readable: " + Utility.getReadableLocationFromSpotID(mContext, spotID));

            Vector<ContentValues> contentValuesVec = new Vector<>(forecastJson.length());


            for (int i = 0; i < forecastJson.length(); i++) {

                // The values we will extract
                long timestamp;
                String dayName;
                int fadedRating, solidRating;
                Double minBreak, maxBreak;
                String swellUnits;
                double primaryHeight;
                int primaryPeriod;
                double primaryDirection;
                String primaryCompassDirection;
                int windSpeed;
                int windDirection;
                String windCompassDirection;
                int windChill;
                int windGusts;
                String windUnits;
                int conditionPressure, conditionTemperature, conditionWeather;
                String conditionUnitPressure, conditionUnit;
                String chartsSwell, chartsPeriod, chartsWind, chartsPressure;

                JSONObject currentForecast = forecastJson.getJSONObject(i);

                timestamp = currentForecast.getLong(MSW_TIME);
                timestamp = timestamp * 1000L; //DateFormat expects time in milliseconds
                dayName = Utility.getDayNameFromTimestamp(timestamp);
                fadedRating = currentForecast.getInt(MSW_FADED_RATING);
                solidRating = currentForecast.getInt(MSW_SOLID_RATING);

                JSONObject currentSwell = currentForecast.getJSONObject(MSW_SWELL);
                minBreak = currentSwell.getDouble(MSW_SWELL_MIN_BREAK);
                maxBreak = currentSwell.getDouble(MSW_SWELL_MAX_BREAK);
                swellUnits = currentSwell.getString(MSW_SWELL_UNITS);

                JSONObject currentSwellComponents = currentSwell.getJSONObject(MSW_SWELL_COMPONENTS);
                JSONObject currentSwellPrimary
                        = currentSwellComponents.getJSONObject(MSW_SWELL_COMPONENTS_PRIMARY);
                primaryHeight = currentSwellPrimary.getDouble(MSW_SWELL_COMPONENTS_PRIMARY_HEIGHT);
                primaryPeriod = currentSwellPrimary.getInt(MSW_SWELL_COMPONENTS_PRIMARY_PERIOD);
                primaryDirection
                        = currentSwellPrimary.getDouble(MSW_SWELL_COMPONENTS_PRIMARY_DIRECTION);
                primaryCompassDirection
                        = currentSwellPrimary.getString(MSW_SWELL_COMPONENTS_PRIMARY_COMPASS_DIRECTION);

                JSONObject currentWind = currentForecast.getJSONObject(MSW_WIND);
                windSpeed = currentWind.getInt(MSW_WIND_SPEED);
                windDirection = currentWind.getInt(MSW_WIND_DIRECTION);
                windCompassDirection = currentWind.getString(MSW_WIND_COMPASS_DIRECTION);
                windChill = currentWind.getInt(MSW_WIND_CHILL);
                windGusts = currentWind.getInt(MSW_WIND_GUSTS);
                windUnits = currentWind.getString(MSW_WIND_UNITS);

                JSONObject currentCondition = currentForecast.getJSONObject(MSW_CONDITION);
                conditionPressure = currentCondition.getInt(MSW_CONDITION_PRESSURE);
                conditionTemperature = currentCondition.getInt(MSW_CONDITION_TEMPERATURE);
                conditionWeather = currentCondition.getInt(MSW_CONDITION_WEATHER);
                conditionUnitPressure = currentCondition.getString(MSW_CONDITION_UNIT_PRESSURE);
                conditionUnit = currentCondition.getString(MSW_CONDITION_UNITS);

                JSONObject currentCharts = currentForecast.getJSONObject(MSW_CHARTS);
                chartsSwell = currentCharts.getString(MSW_CHARTS_SWELL);
                chartsPeriod = currentCharts.getString(MSW_CHARTS_PERIOD);
                chartsWind = currentCharts.getString(MSW_CHARTS_WIND);
                chartsPressure = currentCharts.getString(MSW_CHARTS_PRESSURE);

                ContentValues forecastValues = new ContentValues();

                forecastValues.put(ForecastEntry.COLUMN_LOC_KEY, locationID);
                forecastValues.put(ForecastEntry.COLUMN_LOCAL_TIMESTAMP, timestamp);
                forecastValues.put(ForecastEntry.COLUMN_DAY_NAME, dayName);
                forecastValues.put(ForecastEntry.COLUMN_FADED_RATING, fadedRating);
                forecastValues.put(ForecastEntry.COLUMN_SOLID_RATING, solidRating);
                forecastValues.put(ForecastEntry.COLUMN_SWELL_MIN_BREAK, minBreak);
                forecastValues.put(ForecastEntry.COLUMN_SWELL_MAX_BREAK, maxBreak);
                forecastValues.put(ForecastEntry.COLUMN_SWELL_UNITS, swellUnits);
                forecastValues.put(ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_HEIGHT, primaryHeight);
                forecastValues.put(ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_PERIOD, primaryPeriod);
                forecastValues.put(ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_DIRECTION, primaryDirection);
                forecastValues.put(ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_COMPASS_DIRECTION, primaryCompassDirection);
                forecastValues.put(ForecastEntry.COLUMN_WIND_SPEED, windSpeed);
                forecastValues.put(ForecastEntry.COLUMN_WIND_DIRECTION, windDirection);
                forecastValues.put(ForecastEntry.COLUMN_WIND_COMPASS_DIRECTION, windCompassDirection);
                forecastValues.put(ForecastEntry.COLUMN_WIND_CHILL, windChill);
                forecastValues.put(ForecastEntry.COLUMN_WIND_GUSTS, windGusts);
                forecastValues.put(ForecastEntry.COLUMN_WIND_UNITS, windUnits);
                forecastValues.put(ForecastEntry.COLUMN_CONDITION_PRESSURE, conditionPressure);
                forecastValues.put(ForecastEntry.COLUMN_CONDITION_TEMPERATURE, conditionTemperature);
                forecastValues.put(ForecastEntry.COLUMN_CONDITION_WEATHER, conditionWeather);
                forecastValues.put(ForecastEntry.COLUMN_CONDITION_UNIT_PRESSURE, conditionUnitPressure);
                forecastValues.put(ForecastEntry.COLUMN_CONDITION_UNITS, conditionUnit);
                forecastValues.put(ForecastEntry.COLUMN_CHARTS_SWELL, chartsSwell);
                forecastValues.put(ForecastEntry.COLUMN_CHARTS_PERIOD, chartsPeriod);
                forecastValues.put(ForecastEntry.COLUMN_CHARTS_WIND, chartsWind);
                forecastValues.put(ForecastEntry.COLUMN_CHARTS_PRESSURE, chartsPressure);

                contentValuesVec.add(forecastValues);

            }

            // add to database
            int numInserted = 0;
            if ( contentValuesVec.size() > 0 ) {
                ContentValues[] contentValuesArray = new ContentValues[contentValuesVec.size()];
                contentValuesVec.toArray(contentValuesArray);
                numInserted = mContext.getContentResolver().bulkInsert(ForecastEntry.CONTENT_URI, contentValuesArray);
            }
            Log.d(LOG_TAG, "FetchWeatherTask Complete. "+numInserted+" entries inserted to Db.");


        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected Void doInBackground(String... params) {

        // Declared outside the try/catch so they can be closed in the finally.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        //query parameters for the url
        final String spot_id = params[0];
        final String units = params[1];

        try {
            // Construct the URL for the OpenWeatherMap query
            final String AUTHORITY = "magicseaweed.com";
            final String API_KEY = ApiKey.API_KEY;
            final String END_URL_PATH = "forecast";

            // Best practice to only request the data we actually use. Build the filter string
            final String FILTER_TOP_LEVEL = "localTimestamp,fadedRating,solidRating,";
            final String FILTER_SWELL = "swell.minBreakingHeight,swell.maxBreakingHeight,swell.unit,";
            final String FILTER_SWELL_COMPONENTS = "swell.components.primary.*,";
            final String FILTER_WIND = "wind.*,";
            final String FILTER_CONDITION = "condition.*,";
            final String FILTER_CHARTS = "charts.swell,charts.period,charts.wind,charts.pressure";
            final String FILTER_STRING=FILTER_TOP_LEVEL+FILTER_SWELL+FILTER_SWELL_COMPONENTS
                    +FILTER_WIND+FILTER_CONDITION+FILTER_CHARTS;

            // http://magicseaweed.com/api/API_KEY/forecast/?spot_id=50&units=eu
            // http://magicseaweed.com/api/API_KEY/forecast?spot_id=52&units=eu&fields=localTimestamp%2CfadedRating%2CsolidRating%2Cswell.minBreakingHeight%2Cswell.maxBreakingHeight%2Cswell.unit%2Cswell.components.primary.*%2Cwind.*%2Ccondition.*%2Ccharts.swell%2Ccharts.period%2Ccharts.wind%2Ccharts.pressure
            Uri.Builder builder = new Uri.Builder();
            builder.scheme("http").authority(AUTHORITY)
                    .appendPath("api").appendPath(API_KEY).appendPath(END_URL_PATH)
                    .appendQueryParameter("spot_id", spot_id)
                    .appendQueryParameter("units", units)
                    .appendQueryParameter("fields", FILTER_STRING);

            URL url = new URL(builder.build().toString());
            Log.d(LOG_TAG, "Forecast URL: "+url.toString());

            // Create the request to MagicSeaweed, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // response code
            int response = urlConnection.getResponseCode();
            Log.d(LOG_TAG, "response is: " + response);

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();

            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            forecastJsonStr = buffer.toString();
            Log.v(LOG_TAG, forecastJsonStr);

        } catch (SocketTimeoutException e) {
            Log.e(LOG_TAG, "Network timeout fetching forecast");
            return null;

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error fetching forecast", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            return null;

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        try {
            return getForecastFromJSON(forecastJsonStr, spot_id);

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        //only happens in case of errors.
        return null;
    }


}
