package com.example.rloftus88.wavewatcher;


import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class SettingsActivity extends PreferenceActivity implements Preference.OnPreferenceChangeListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);

        bindPreferencesSummaryToValue(findPreference(getString(R.string.pref_display_units_key)));
        bindPreferencesSummaryToValue(findPreference(getString(R.string.pref_location_key)));

    }

    private void bindPreferencesSummaryToValue(Preference preference) {

        // set the listener to watch for changes
        preference.setOnPreferenceChangeListener(this);

        // trigger listener immediately with preference's current value
        onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {

        String stringValue = value.toString();

        if (preference instanceof ListPreference) {

            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);

            if (prefIndex >=0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            } else {
                preference.setSummary(stringValue);
            }
        }
        return true;
    }
}
