package com.hotcats.textreminder;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
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

    public static final String PREF_ENABLED = "pref_enabled";
    public static final String PREF_REPEAT_DELAY = "pref_repeatDelay";
    public static final String PREF_CANCEL_NOTIFICATION_ENABLED = "pref_cancelNotificationEnabled";

    private ListPreference repeatDelayPreference;

    private boolean enabledDefault;
    private String repeatDelayDefault;
    private boolean canceledNotificationEnabledDefault;

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

        Resources res = getResources();
        enabledDefault = res.getBoolean(R.bool.pref_enabled_default);
        repeatDelayDefault = res.getString(R.string.pref_repeatDelay_default);
        canceledNotificationEnabledDefault = res.getBoolean(R.bool.pref_cancelNotificationEnabled_default);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        Object newValue = null;

        if (PREF_ENABLED.equals(key)) {
            newValue = sharedPreferences.getBoolean(key, enabledDefault);
            boolean enabled = (Boolean) newValue;
            if (!enabled) {
                // Stop current alarm, if one is set.
                AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                Utilities.cancelAll(this, am);
                Log.i("preferences", "canceled current alarm and notification (if set)");
            }
        } else if (PREF_REPEAT_DELAY.equals(key)) {
            newValue = sharedPreferences.getString(key, repeatDelayDefault);
            updateRepeatDelaySummary();
        } else if (PREF_CANCEL_NOTIFICATION_ENABLED.equals(key)) {
            newValue = sharedPreferences.getBoolean(key, canceledNotificationEnabledDefault);
            boolean enabled = (Boolean) newValue;
            if (!enabled) {
                Utilities.cancelNotification(this);
            }
        }

        Log.i("preferences", "preference " + key + " changed to " + newValue);
    }

    /**
     * Sets the summary of the repeatDelay preference to be the value of the
     * currently selected entry.
     */
    private void updateRepeatDelaySummary() {
        repeatDelayPreference.setSummary(repeatDelayPreference.getEntry());
    }
}
