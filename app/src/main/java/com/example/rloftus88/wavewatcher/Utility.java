package com.example.rloftus88.wavewatcher;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {

    private static final String LOG_TAG = Utility.class.getSimpleName();

    public static String getPreferredLocation (Context context) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPrefs.getString(
                context.getString(R.string.pref_location_key),
                context.getString(R.string.pref_location_default));

    }

    public static String getReadablePreferredLocation (Context context) {

        String prefValue = getPreferredLocation(context);
        CharSequence[] keys = context.getApplicationContext().getResources()
                .getTextArray(R.array.pref_location_titles);
        CharSequence[] values = context.getApplicationContext().getResources()
                .getTextArray(R.array.pref_location_values);

        for (int i=0; i<values.length; i++) {
            if (values[i].equals(prefValue)) {
                return (String) keys[i];
            }
        }
        // in case of failure we get a nice (hopefully) easter egg.
        return "mordor";
    }

    public static String getReadableLocationFromSpotID (Context context, String spotID) {

        CharSequence[] keys = context.getApplicationContext().getResources()
                .getTextArray(R.array.pref_location_titles);
        CharSequence[] values = context.getApplicationContext().getResources()
                .getTextArray(R.array.pref_location_values);

        for (int i=0; i<values.length; i++) {
            if (values[i].equals(spotID)) {
                return (String) keys[i];
            }
        }
        // in case of failure we get a nice (hopefully) easter egg.
        return "Valhalla";
    }

    public static void openPreferredLocationInMap(Context context) {

        String location = getReadablePreferredLocation(context).replaceAll("\\s", "+");
        Log.d(LOG_TAG, location);

        Uri geoLocation = Uri.parse("geo:0,0?").buildUpon()
                .appendQueryParameter("q", location).build();

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);

        } else {
            Log.d(LOG_TAG, "Couldn't call \"" + location + "\", no receiving apps installed!");
            String userMessage = "No maps application installed!\nThis feature opens your current " +
                    "location setting in any installed maps application.";
            Toast toast = Toast.makeText(context.getApplicationContext(), userMessage, Toast.LENGTH_LONG);
            // Center the text in the Toast
            TextView view = (TextView) toast.getView().findViewById(android.R.id.message);
            if (null != view) {
                view.setGravity(Gravity.CENTER);
            }
            toast.show();
        }
    }

    public static String getFriendlyDayName(Context context, long dateInMillis) {

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if ( julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        } else {
            return getDayNameFromTimestamp(dateInMillis);
        }
    }


    public static String getDayNameFromTimestamp (long timestamp) {
        Date date = new Date(timestamp);
        return new SimpleDateFormat("cccc").format(date);
    }

    public static String getDateFromTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        return DateFormat.getDateInstance().format(date);
    }

    public static String getTimeFromTimestamp(long timestamp) {
        Date date = new Date(timestamp);
        return new SimpleDateFormat("kk:mm").format(date);
    }

    public static String getFriendlyDayAndTimeFromtimestamp(Context context, long timestamp) {
        String day = Utility.getFriendlyDayName(context, timestamp);
        String time = Utility.getTimeFromTimestamp(timestamp);

        return day+"\n"+time;
    }

    public static String getFriendlyDayAndDateFromtimestamp(Context context, long timestamp) {
        String day = Utility.getFriendlyDayName(context, timestamp);
        String date = Utility.getDateFromTimestamp(timestamp);

        return day+"\n"+date;
    }

    public static String formatTemperature (Context context, int temperature, String Unit) {
        return context.getString(R.string.format_temperature, temperature)+Unit;
    }

    public static String formatMaxBreak (Context context, double maxBreak, String Unit) {
        return context.getString(R.string.format_max_break, maxBreak, Unit);
    }


    /*
    public static int getIconResourceForWindDirection(double windDirection) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (windDirection >= 0 && windDirection < 5) {
            return R.drawable.ic_storm;
        } else if (windDirection >= 5 && windDirection < 10) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 10 && windDirection < 15) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 15 && windDirection < 20) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 20 && windDirection < 25) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 25 && windDirection < 30) {
            return R.drawable.ic_light_rain;
        } else if (windDirection >= 30 && windDirection < 35) {
            return R.drawable.ic_rain;
        } else if (windDirection >= 35 && windDirection < 40) {
            return R.drawable.ic_snow;
        } else if (windDirection >= 40 && windDirection < 45) {
            return R.drawable.ic_rain;
        } else if (windDirection >= 45 && windDirection < 50) {
            return R.drawable.ic_snow;
        } else if (windDirection >= 50 && windDirection < 55) {
            return R.drawable.ic_fog;
        } else if (windDirection >= 55 && windDirection < 60) {
            return R.drawable.ic_storm;
        } else if (windDirection >= 60 && windDirection < 65) {
            return R.drawable.ic_clear;
        } else if (windDirection >= 65 && windDirection < 70) {
            return R.drawable.ic_light_clouds;
        } else if (windDirection >= 70 && windDirection < 75) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 75 && windDirection < 80) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 80 && windDirection < 85) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 85 && windDirection < 90) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 90 && windDirection < 80) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 95 && windDirection < 80) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 100 && windDirection < 80) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 105 && windDirection < 80) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 110 && windDirection < 80) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 115 && windDirection < 80) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 120 && windDirection < 80) {
            return R.drawable.ic_cloudy;
        } else if (windDirection >= 125 && windDirection < 80) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    public static int getIconResourceForWaveDirection(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }


    public static int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId == 1 ) {
            return R.drawable.ic_storm;
        } else if (weatherId == 2) {
            return R.drawable.ic_light_rain;
        } else if (weatherId == 3) {
            return R.drawable.ic_rain;
        } else if (weatherId == 4) {
            return R.drawable.ic_snow;
        } else if (weatherId == 5) {
            return R.drawable.ic_rain;
        } else if (weatherId == 6) {
            return R.drawable.ic_snow;
        } else if (weatherId == 7) {
            return R.drawable.ic_fog;
        } else if (weatherId == 8) {
            return R.drawable.ic_storm;
        } else if (weatherId == 9) {
            return R.drawable.ic_clear;
        } else if (weatherId == 10) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 11) {
            return R.drawable.ic_cloudy;
        } else if (weatherId == 12) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 13) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 14) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 15) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 16) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 17) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 18) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 19) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 20) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 21) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 22) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 23) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 24) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 25) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 26) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 27) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 28) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 29) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 30) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 31) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 32) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 33) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 34) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 35) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 36) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 37) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId == 38) {
            return R.drawable.ic_light_clouds;
        }
        return -1;
    }
*/
}
