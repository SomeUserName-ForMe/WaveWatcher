package com.example.rloftus88.wavewatcher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rloftus88.wavewatcher.data.ForecastContract;
import com.example.rloftus88.wavewatcher.data.ForecastContract.ForecastEntry;


public class WeekForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = WeekForecastFragment.class.getSimpleName();

    // must be unique for each loader per activity
    private static final int WEEK_FORECAST_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            ForecastEntry.TABLE_NAME+"."+ ForecastEntry._ID,
            ForecastEntry.COLUMN_LOCAL_TIMESTAMP,
            ForecastEntry.COLUMN_DAY_NAME,
            ForecastEntry.COLUMN_FADED_RATING,
            ForecastEntry.COLUMN_SOLID_RATING,
            ForecastEntry.COLUMN_SWELL_MIN_BREAK,
            ForecastEntry.COLUMN_SWELL_MAX_BREAK,
            ForecastEntry.COLUMN_SWELL_UNITS,
            ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_PERIOD,
            ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_DIRECTION,
            ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_COMPASS_DIRECTION,
            ForecastEntry.COLUMN_WIND_SPEED,
            ForecastEntry.COLUMN_WIND_DIRECTION,
            ForecastEntry.COLUMN_WIND_COMPASS_DIRECTION,
            ForecastEntry.COLUMN_WIND_GUSTS,
            ForecastEntry.COLUMN_WIND_UNITS,
    };
    static final int COL_FORECAST_ID = 0;
    static final int COL_LOCAL_TIMESTAMP = 1;
    static final int COL_DAY_NAME = 2;
    static final int COL_FADED_RATING = 3;
    static final int COL_SOLID_RATING = 4;
    static final int COL_SWELL_MIN_BREAK = 5;
    static final int COL_SWELL_MAX_BREAK = 6;
    static final int COL_SWELL_UNITS = 7;
    static final int COL_SWELL_PRIMARY_PERIOD = 8;
    static final int COL_SWELL_PRIMARY_DIRECTION = 9;
    static final int COL_SWELL_PRIMARY_COMPASS_DIRECTION = 10;
    static final int COL_WIND_SPEED = 11;
    static final int COL_WIND_DIRECTION = 12;
    static final int COL_WIND_COMPASS_DIRECTION = 13;
    static final int COL_WIND_GUSTS = 14;
    static final int COL_WIND_UNITS = 15;


    private WeekForecastAdapter mWeekForecastAdapter;

    public WeekForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_weekforecast, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateForecast();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        mWeekForecastAdapter = new WeekForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.frag_week_forecast, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.week_fragment_list_view);
        listView.setAdapter(mWeekForecastAdapter);

        // We'll call our MainActivity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    String dayName = cursor.getString(COL_DAY_NAME);
                    Intent intent;
                    if (position == 0) {
                        intent = new Intent(getActivity(), DetailActivity.class)
                                .setData(ForecastContract.ForecastEntry.buildWeatherLocationWithDayAndStartTime(
                                        locationSetting, dayName, cursor.getLong(COL_LOCAL_TIMESTAMP)
                                )).putExtra("TIMESTAMP", cursor.getLong(COL_LOCAL_TIMESTAMP));
                    } else {
                        intent = new Intent(getActivity(), DetailActivity.class)
                                .setData(ForecastContract.ForecastEntry.buildWeatherLocationWithDay(
                                        locationSetting, dayName
                                )).putExtra("TIMESTAMP", cursor.getLong(COL_LOCAL_TIMESTAMP));
                    }
                    Log.d(LOG_TAG, "URI is: "+intent.getDataString());
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(WEEK_FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    void onLocationChanged() {
        updateForecast();
        getLoaderManager().restartLoader(WEEK_FORECAST_LOADER, null, this);
    }

    public boolean isOnline() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void updateForecast() {

        if (isOnline()) {

            FetchForecastTask forecastTask = new FetchForecastTask(getActivity());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            String spotID = preferences.getString(
                    getString(R.string.pref_location_key), getString(R.string.pref_location_default));
            String units = preferences.getString(
                    getString(R.string.pref_display_units_key), getString(R.string.pref_display_units_default));

            forecastTask.execute(spotID, units);

        } else {
            Context context = getActivity().getApplicationContext();
            Toast.makeText(context, "No Network Connection", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String spotID = Utility.getPreferredLocation(getActivity());
        //TODO make a proper query for week view


        // Sort order:  Ascending, by date.
        String sortOrder = ForecastEntry.COLUMN_LOCAL_TIMESTAMP + " ASC";
        // Starting from 3hrs before current time
        final long millisIn3Hrs = 3*60*60*1000L;
        Uri weatherForLocationUri = ForecastEntry.buildWeatherLocationWithStartTime(
                spotID, System.currentTimeMillis() - millisIn3Hrs);


        return new CursorLoader(getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder/*+" LIMIT 5"*/);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mWeekForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mWeekForecastAdapter.swapCursor(null);
    }
}
