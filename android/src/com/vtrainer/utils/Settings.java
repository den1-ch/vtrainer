package com.vtrainer.utils;

import android.content.Context;
import android.preference.PreferenceManager;

import com.vtrainer.provider.VocabularyMetaData;

public class Settings {
    private static final String DEFAULT_TARGET_LANGUAGE = "eng";
    private static final String TARGET_LANGUAGE_KEY = "targetLanguage";

    public static final String ENG_TARGET_LANGUAGE = "eng";  //TODO think how can avoid this
    public static final String IT_TARGET_LANGUAGE = "it";

    public static String getTargetLanguage(final Context context) {
        return  PreferenceManager.getDefaultSharedPreferences(context).getString(TARGET_LANGUAGE_KEY, DEFAULT_TARGET_LANGUAGE);
    }

    public static int getCurrentVocabularyId() {
        return VocabularyMetaData.MAIN_VOCABULARY_ID;
    }
}