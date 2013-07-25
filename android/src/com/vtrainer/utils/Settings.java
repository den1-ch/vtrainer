package com.vtrainer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    private static final String DEFAULT_TARGET_LANGUAGE = "eng";
    private static final String TARGET_LANGUAGE_KEY = "targetLanguage";

    public static final String ENG_TARGET_LANGUAGE = "eng";  //TODO think how can avoid this
    public static final String IT_TARGET_LANGUAGE = "it";
    
    private SharedPreferences preferences;

    public Settings(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String getTargetLanguage() {
        return preferences.getString(TARGET_LANGUAGE_KEY, DEFAULT_TARGET_LANGUAGE);
    }
}