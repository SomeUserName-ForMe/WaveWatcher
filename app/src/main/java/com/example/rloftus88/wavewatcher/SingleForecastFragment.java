package com.example.rloftus88.wavewatcher;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rloftus88.wavewatcher.data.ForecastContract;

public class SingleForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = SingleForecastFragment.class.getSimpleName();
    private static final String TEMPORARY_HASHTAG = "#WaveWatcher";

    private ShareActionProvider mShareActionProvider;
    private String mForecastStr;

    // must be unique for each loader per activity
    private static final int SINGLE_FORECAST_LOADER = 2;

    private static final String[] FORECAST_COLUMNS = {
            ForecastContract.ForecastEntry.TABLE_NAME+"."+ ForecastContract.ForecastEntry._ID,
            ForecastContract.ForecastEntry.COLUMN_LOCAL_TIMESTAMP,
            ForecastContract.ForecastEntry.COLUMN_DAY_NAME,
            ForecastContract.ForecastEntry.COLUMN_FADED_RATING,
            ForecastContract.ForecastEntry.COLUMN_SOLID_RATING,
            ForecastContract.ForecastEntry.COLUMN_SWELL_MIN_BREAK,
            ForecastContract.ForecastEntry.COLUMN_SWELL_MAX_BREAK,
            ForecastContract.ForecastEntry.COLUMN_SWELL_UNITS,
            ForecastContract.ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_HEIGHT,
            ForecastContract.ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_PERIOD,
            ForecastContract.ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_DIRECTION,
            ForecastContract.ForecastEntry.COLUMN_SWELL_COMPONENTS_PRIMARY_COMPASS_DIRECTION,
            ForecastContract.ForecastEntry.COLUMN_WIND_SPEED,
            ForecastContract.ForecastEntry.COLUMN_WIND_DIRECTION,
            ForecastContract.ForecastEntry.COLUMN_WIND_COMPASS_DIRECTION,
            ForecastContract.ForecastEntry.COLUMN_WIND_CHILL,
            ForecastContract.ForecastEntry.COLUMN_WIND_GUSTS,
            ForecastContract.ForecastEntry.COLUMN_WIND_UNITS,
            ForecastContract.ForecastEntry.COLUMN_CHARTS_SWELL,
            ForecastContract.ForecastEntry.COLUMN_CHARTS_PERIOD,
            ForecastContract.ForecastEntry.COLUMN_CHARTS_WIND,
            ForecastContract.ForecastEntry.COLUMN_CHARTS_PRESSURE
    };
    static final int COL_FORECAST_ID = 0;
    static final int COL_LOCAL_TIMESTAMP = 1;
    static final int COL_DAY_NAME = 2;
    static final int COL_FADED_RATING = 3;
    static final int COL_SOLID_RATING = 4;
    static final int COL_SWELL_MIN_BREAK = 5;
    static final int COL_SWELL_MAX_BREAK = 6;
    static final int COL_SWELL_UNITS = 7;
    static final int COL_SWELL_PRIMARY_HEIGHT = 8;
    static final int COL_SWELL_PRIMARY_PERIOD = 9;
    static final int COL_SWELL_PRIMARY_DIRECTION = 10;
    static final int COL_SWELL_PRIMARY_COMPASS_DIRECTION = 11;
    static final int COL_WIND_SPEED = 12;
    static final int COL_WIND_DIRECTION = 13;
    static final int COL_WIND_COMPASS_DIRECTION = 14;
    static final int COL_WIND_CHILL = 15;
    static final int COL_WIND_GUSTS = 16;
    static final int COL_WIND_UNITS = 17;
    static final int COL_CHARTS_SWELL = 18;
    static final int COL_CHARTS_PERIOD = 19;
    static final int COL_CHARTS_WIND = 20;
    static final int COL_CHARTS_PRESSURE = 21;

    private TextView mWaveHeadingView;
    private ImageView mWaveDirectionImageView;
    private TextView mWaveCompassDirectionView;
    private TextView mWaveMaxBreakView;
    private TextView mWaveMinBreakView;
    private TextView mWaveAvgBreakView;
    private TextView mWavePeriodView;

    private TextView mWindHeadingView;
    private ImageView mWindDirectionImageView;
    private TextView mWindCompassDirectionView;
    private TextView mWindSpeedView;
    private TextView mWindGustsView;
    private TextView mWindChillView;

    private TextView mWeatherHeadingView;
    private ImageView mWeatherIconImageView;
    private TextView mWeatherTemperatureView;
    private TextView mWeatherPressureView;


    public SingleForecastFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frag_single_forecast, container, false);

        mWaveHeadingView = (TextView) rootView.findViewById(R.id.single_forecast_waves_heading_textview);
        mWaveDirectionImageView = (ImageView) rootView.findViewById(R.id.single_forecast_waves_direction_imageview);
        mWaveCompassDirectionView = (TextView) rootView.findViewById(R.id.single_forecast_waves_compass_direction_textview);
        mWaveMaxBreakView = (TextView) rootView.findViewById(R.id.single_forecast_waves_max_break_textview);
        mWaveMinBreakView = (TextView) rootView.findViewById(R.id.single_forecast_waves_min_break_textview);
        mWaveAvgBreakView = (TextView) rootView.findViewById(R.id.single_forecast_waves_average_textview);
        mWavePeriodView = (TextView) rootView.findViewById(R.id.single_forecast_waves_period_textview);

        mWindHeadingView = (TextView) rootView.findViewById(R.id.single_forecast_wind_heading_textview);
        mWindDirectionImageView = (ImageView) rootView.findViewById(R.id.single_forecast_wind_direction_imageview);
        mWindCompassDirectionView = (TextView) rootView.findViewById(R.id.single_forecast_wind_compass_direction_textview);
        mWindSpeedView = (TextView) rootView.findViewById(R.id.single_forecast_wind_speed_textview);
        mWindGustsView = (TextView) rootView.findViewById(R.id.single_forecast_wind_gusts_textview);
        mWindChillView = (TextView) rootView.findViewById(R.id.single_forecast_wind_chill_textview);

        mWeatherHeadingView = (TextView) rootView.findViewById(R.id.single_forecast_weather_heading_textview);
        mWeatherIconImageView = (ImageView) rootView.findViewById(R.id.single_forecast_weather_icon_imageview);
        mWeatherTemperatureView = (TextView) rootView.findViewById(R.id.single_forecast_weather_temperature_textview);
        mWeatherPressureView = (TextView) rootView.findViewById(R.id.single_forecast_weather_pressure_textview);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SINGLE_FORECAST_LOADER, savedInstanceState, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.fragment_singleforecast, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mForecastStr != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share action provider is null...");
        }

    }

    private Intent createShareForecastIntent() {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecastStr+TEMPORARY_HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Intent intent = getActivity().getIntent();
        if ((null == intent) || (null == intent.getData())) {
            return null;
        }

        return new CursorLoader(
                getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if (data != null && data.moveToFirst()) {

            long timestamp = data.getLong(COL_LOCAL_TIMESTAMP);
            mForecastStr = Utility.getReadablePreferredLocation(getActivity()) + " "
                    + Utility.getDayNameFromTimestamp(timestamp) + " "
                    + Utility.getTimeFromTimestamp(timestamp);


            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
