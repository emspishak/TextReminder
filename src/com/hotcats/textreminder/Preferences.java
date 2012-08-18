package com.hotcats.textreminder;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Handles preferences.
 */
public class Preferences extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
