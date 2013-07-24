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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        ListPreference listPreference = (ListPreference) findPreference(TARGET_LANGUAGE_PREFARENCE_KEY);
        listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Uri uri = Uri.withAppendedPath(VTrainerDatabase.BASE_URI, Constants.TARGET_LANGUAGE_CHANGED_PATH);
                getContentResolver().update(uri, null, null, null);
                
                return true;
            }
        });
    }
}