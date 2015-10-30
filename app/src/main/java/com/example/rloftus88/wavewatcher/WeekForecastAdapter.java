package com.example.rloftus88.wavewatcher;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link WeekForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class WeekForecastAdapter extends CursorAdapter {

    private Context mContext;

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;

    public WeekForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == 0)? VIEW_TYPE_TODAY: VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }




    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId;
        layoutId = (viewType == 0 )?
                R.layout.list_item_week_forecast_today:
                R.layout.list_item_week_forecast;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);

        if (layoutId == R.layout.list_item_week_forecast_today) {
            ViewHolderToday viewHolderToday = new ViewHolderToday(view);
            view.setTag(viewHolderToday);
        } else {
            ViewHolder viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        int viewType = getItemViewType(cursor.getPosition());

        if (viewType == 0) {
            ViewHolderToday viewHolderToday = (ViewHolderToday) view.getTag();

            long timestamp = cursor.getLong(WeekForecastFragment.COL_LOCAL_TIMESTAMP);
            viewHolderToday.timeDayTextView.setText(Utility.getFriendlyDayAndTimeFromtimestamp(mContext, timestamp));

            double maxBreak = cursor.getDouble(WeekForecastFragment.COL_SWELL_MAX_BREAK);
            double minBreak = cursor.getDouble(WeekForecastFragment.COL_SWELL_MIN_BREAK);
            String swellUnits = cursor.getString(WeekForecastFragment.COL_SWELL_UNITS);
            viewHolderToday.maxBreakTextView.setText("Max Break: "+maxBreak+swellUnits); //Utility.formatMaxBreak(mContext, maxBreak, swellUnits)
            viewHolderToday.minBreakTextView.setText("Min Break: "+minBreak+swellUnits);
            // TODO add swell arrows
            String swellCompassDir = cursor.getString(WeekForecastFragment.COL_SWELL_PRIMARY_COMPASS_DIRECTION);
            viewHolderToday.waveCompassDirTextview.setText(swellCompassDir);
            int wavePeriod = cursor.getInt(WeekForecastFragment.COL_SWELL_PRIMARY_PERIOD);
            viewHolderToday.periodTextView.setText("Period: "+wavePeriod);
//            viewHolderToday.periodTextView.setText(Utility.formatTemperature(mContext, wavePeriod, swellUnits));

            // TODO add wind arrows
            int windSpeed = cursor.getInt(WeekForecastFragment.COL_WIND_SPEED);
            String windCompassDir = cursor.getString(WeekForecastFragment.COL_WIND_COMPASS_DIRECTION);
            int windGusts = cursor.getInt(WeekForecastFragment.COL_WIND_GUSTS);
            String windUnits = cursor.getString(WeekForecastFragment.COL_WIND_UNITS);
            viewHolderToday.windSpeedAndCompassTextview.setText(windSpeed+windUnits+" "+windCompassDir);
            viewHolderToday.windGustsTextView.setText("Gusts: "+windGusts+windUnits);

        } else {
            ViewHolder viewHolder = (ViewHolder) view.getTag();

            long timestamp = cursor.getLong(WeekForecastFragment.COL_LOCAL_TIMESTAMP);
            viewHolder.textView.setText(Utility.getFriendlyDayAndDateFromtimestamp(mContext, timestamp));
        }
    }

    public static class ViewHolder {
        
        public final TextView textView;
        
        public ViewHolder(View view) {
            textView = (TextView) view.findViewById(R.id.week_forecast_list_textview);
        }
    }

    public static class ViewHolderToday {

        public final TextView maxBreakTextView;
        public final TextView minBreakTextView;
        public final TextView periodTextView;
        public final ImageView waveDirectionImageView;
        public final TextView waveCompassDirTextview;

        public final ImageView windDirectionImageView;
        public final TextView windSpeedAndCompassTextview;
        public final TextView windGustsTextView;

        public final TextView timeDayTextView;

        public ViewHolderToday(View view) {

            maxBreakTextView = (TextView) view.findViewById(R.id.week_frag_today_max_break_textview);
            minBreakTextView = (TextView) view.findViewById(R.id.week_frag_today_min_break_textview);
            periodTextView = (TextView) view.findViewById(R.id.week_frag_today_swell_period_textview);
            waveDirectionImageView = (ImageView) view.findViewById(R.id.week_frag_today_swell_direction_imageview);
            waveCompassDirTextview = (TextView) view.findViewById(R.id.week_frag_today_swell_compass_direction_textview);

            windDirectionImageView = (ImageView) view.findViewById(R.id.week_frag_today_wind_direction_imageview);
            windSpeedAndCompassTextview = (TextView) view.findViewById(R.id.week_frag_today_wind_textview);
            windGustsTextView = (TextView) view.findViewById(R.id.week_frag_today_wind_gusts_textview);

            timeDayTextView = (TextView) view.findViewById(R.id.week_frag_today_time_textview);
        }
    }

}
