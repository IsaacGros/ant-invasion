package com.examples.hm14_gros;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class PrefsFragmentSettings extends PreferenceFragment {
    public PrefsFragmentSettings() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preference from an XML resource
        addPreferencesFromResource(R.xml.prefs_fragment_settings);

        // Get default shared preferences storage
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Read the saved high score, default to 0 if no score is stored
        String savedHighScore = sharedPrefs.getString("highScore", "0");

        // Find specific High Score line inside the XML view layer
        Preference highScorePref = findPreference("highScore");

        // Set the summary text box to show the score value instead of hard-coded text
        if (highScorePref != null) {
            highScorePref.setSummary(savedHighScore);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}