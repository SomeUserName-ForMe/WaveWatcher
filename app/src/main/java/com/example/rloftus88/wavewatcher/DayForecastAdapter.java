package com.example.rloftus88.wavewatcher;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link DayForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 */
public class DayForecastAdapter extends CursorAdapter {

    private Context mContext;

    public DayForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatStarRating(int solidRating, int fadedRating) {

        String highLowStr = "Stars: Solid - "+solidRating+", Faded - "+fadedRating;

        return highLowStr;
    }

    private String getTimeAndDayFromtimestamp(long timestamp) {
        String day = Utility.getDayNameFromTimestamp(timestamp);
        String time = Utility.getTimeFromTimestamp(timestamp);

        return time+" "+day;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        // get row indices for our cursor

        String highAndLow = formatStarRating(
                cursor.getInt(DayForecastFragment.COL_SOLID_RATING),
                cursor.getInt(DayForecastFragment.COL_FADED_RATING)
        );

        return getTimeAndDayFromtimestamp(cursor.getLong(WeekForecastFragment.COL_LOCAL_TIMESTAMP)) +" "
                + Utility.getDateFromTimestamp(cursor.getLong(WeekForecastFragment.COL_LOCAL_TIMESTAMP))+"\n"
                + Utility.getReadablePreferredLocation(mContext) +"\n" // can't see weather for any other location in UI
                + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_day_forecast, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        long timestamp = cursor.getLong(DayForecastFragment.COL_LOCAL_TIMESTAMP);
        viewHolder.textView.setText(Utility.getTimeFromTimestamp(timestamp));

    }

    public static class ViewHolder {

        public final TextView textView;

        public ViewHolder(View view) {
            textView = (TextView) view.findViewById(R.id.day_frag_list_item_time);
        }
    }

}
