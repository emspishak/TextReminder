package com.hotcats.textreminder;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;

/**
 * Handles preferences.
 */
public class Preferences extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    public static final String PREF_REPEAT_DELAY = "pref_repeatDelay";

    private ListPreference repeatDelayPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        PreferenceScreen prefs = getPreferenceScreen();
        repeatDelayPreference = (ListPreference) prefs
                .findPreference(PREF_REPEAT_DELAY);
        updateRepeatDelaySummary();
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        String value = sharedPreferences.getString(key, "-1");
        Log.i("preferences", "preference " + key + " changed to " + value);
        if (PREF_REPEAT_DELAY.equals(key)) {
            updateRepeatDelaySummary();
        }
    }

    /**
     * Sets the summary of the repeatDelay preference to be the value of the
     * currently selected entry.
     */
    private void updateRepeatDelaySummary() {
        repeatDelayPreference.setSummary(repeatDelayPreference.getEntry());
    }
}
