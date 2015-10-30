package com.example.rloftus88.wavewatcher;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rloftus88.wavewatcher.data.ForecastContract;


public class DayForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DayForecastFragment.class.getSimpleName();

    // must be unique for each loader per activity
    private static final int DAY_FORECAST_LOADER = 1;

    private static final String[] FORECAST_COLUMNS = {
            ForecastContract.ForecastEntry.TABLE_NAME + "." + ForecastContract.ForecastEntry._ID,
            ForecastContract.ForecastEntry.COLUMN_LOCAL_TIMESTAMP,
            ForecastContract.ForecastEntry.COLUMN_DAY_NAME,
            ForecastContract.ForecastEntry.COLUMN_FADED_RATING,
            ForecastContract.ForecastEntry.COLUMN_SOLID_RATING,
    };
    static final int COL_FORECAST_ID = 0;
    static final int COL_LOCAL_TIMESTAMP = 1;
    static final int COL_DAY_NAME = 2;
    static final int COL_FADED_RATING = 3;
    static final int COL_SOLID_RATING = 4;


    private String mForecastStr;
    private DayForecastAdapter mDayForecastAdapter;

    public DayForecastFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mDayForecastAdapter = new DayForecastAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.frag_day_forecast, container, false);


        Intent intent = getActivity().getIntent();
        long timestamp = -1;
        if (null != intent) {
            timestamp = intent.getLongExtra("TIMESTAMP", 0);
            Log.d(LOG_TAG, "timestamp: " + timestamp);
        }

        String dayAndDate = Utility.getFriendlyDayAndDateFromtimestamp(getActivity(), timestamp);
        TextView timetextView = (TextView) rootView.findViewById(R.id.day_fragment_day_date_textview);
        timetextView.setText(dayAndDate); // should be day name/date
        TextView locationtextView = (TextView) rootView.findViewById(R.id.day_fragment_location_textview);
        locationtextView.setText(Utility.getReadablePreferredLocation(getActivity()));

        ListView listView = (ListView) rootView.findViewById(R.id.day_fragment_list_view);
        listView.setAdapter(mDayForecastAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Intent intent;
                    intent = new Intent(getActivity(), SingleForecastActivity.class)
                            .setData(ForecastContract.ForecastEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_LOCAL_TIMESTAMP)
                            ));
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DAY_FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Intent intent = getActivity().getIntent();
        if (null == intent) {
//        Log.d(LOG_TAG, intent.getDataString());
//        Log.d(LOG_TAG, intent.getData().toString());
            return null;
        }

//        String spotID = Utility.getPreferredLocation(getActivity());
        // Sort order:  Ascending, by date.
        String sortOrder = ForecastContract.ForecastEntry.COLUMN_LOCAL_TIMESTAMP + " ASC";

        return new CursorLoader(getActivity(),
                intent.getData(),
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDayForecastAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDayForecastAdapter.swapCursor(null);
    }
}
