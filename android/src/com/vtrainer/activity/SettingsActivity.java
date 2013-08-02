package com.vtrainer.activity;

import com.vtrainer.R;
import com.vtrainer.provider.VTrainerDatabase;
import com.vtrainer.utils.Constants;

import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {
    private static final String TARGET_LANGUAGE_PREFARENCE_KEY = "targetLanguage";
    private static final Uri TARGET_LANGUAGE_CHANGED_URI = Uri.withAppendedPath(VTrainerDatabase.BASE_URI, Constants.TARGET_LANGUAGE_CHANGED_PATH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        ListPreference listPreference = (ListPreference) findPreference(TARGET_LANGUAGE_PREFARENCE_KEY);
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                getContentResolver().update(TARGET_LANGUAGE_CHANGED_URI, null, null, new String[] {(String)newValue} );
                
                return true;
            }
        });
    }
}