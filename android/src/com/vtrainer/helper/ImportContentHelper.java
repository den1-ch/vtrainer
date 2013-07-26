package com.vtrainer.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.DatabaseUtils.InsertHelper;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.vtrainer.R;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.VocabularyMetaData;
import com.vtrainer.utils.Constants;
import com.vtrainer.utils.Settings;

public class ImportContentHelper {
    private static final String TAG = ImportContentHelper.class.getSimpleName();
    private static final String WORD_DELIMITER = ";"; // TODO move

    private final Context context;
    private final SharedPreferences sharedPreferences;

    public ImportContentHelper(final Context context) {
        this.context = context;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void fillVocabularyStaticData(final SQLiteDatabase db, final String language) {
        if (!isDataInitialized(language)) {
            return;
        }

        InsertHelper insertHelper = new InsertHelper(db, VocabularyMetaData.TABLE_NAME);
        try {
            fillBaseCategoryStaticData(insertHelper, db, language);
            fillCategoriesData(insertHelper, db, language);
        } finally {
            insertHelper.close();

            Editor editor = sharedPreferences.edit();
            editor.putBoolean(language, false);
            editor.commit();
        }
    }

    private void fillBaseCategoryStaticData(final InsertHelper insertHelper, final SQLiteDatabase db, final String language) { //TODO update #3
        Logger.debug(TAG, "Fill vocabulary static data.");
        if (language.equals(Settings.ENG_TARGET_LANGUAGE)) {
            Logger.debug(TAG, "Fill vocabulary static data eng.");
            String[] vocabulary = context.getResources().getStringArray(
                Constants.IS_TEST_MODE ? R.array.test_vocabulary_array : R.array.vocabulary_array);
            fillVocabularyData(insertHelper, vocabulary, VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID, language);
        } else if (language.equals(Settings.IT_TARGET_LANGUAGE)) {
            Logger.debug(TAG, "Fill vocabulary static data it.");
            fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.test_vocabulary_array_it),
                VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID, language);
        } else {
            Logger.debug(TAG, "Static data for target language:" + language + " not avaliable");
        }
    }

    private void fillCategoriesData(final InsertHelper insertHelper, final SQLiteDatabase db, final String language) {
        if (language.equals(Settings.ENG_TARGET_LANGUAGE)) {
            Logger.debug(TAG, "Fill categories static data eng.");
            fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_clothes_array), R.array.cat_clothes_array, language);
            fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_traits_array), R.array.cat_traits_array, language);
            fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_sport_array), R.array.cat_sport_array, language);
            fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_weather_array), R.array.cat_weather_array, language);
            fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_work_array), R.array.cat_work_array, language);
            fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_study_array), R.array.cat_study_array, language);
            fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_body_en), R.array.cat_body_en, language);
            fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_color_en), R.array.cat_color_en, language);
        } else {
            Logger.debug(TAG, "Data for target language:" + language + " not avaliable");
        }
    }

    private void fillVocabularyData(final InsertHelper insertHelper, final String[] data, final int categoryId, final String language) {
        int vocabularyIdIndex = insertHelper.getColumnIndex(VocabularyMetaData.VOCABULARY_ID);
        int categoryIdIndex = insertHelper.getColumnIndex(VocabularyMetaData.CATEGOTY_ID);
        int translationWordIndex = insertHelper.getColumnIndex(VocabularyMetaData.TRANSLATION_WORD);
        int foreignWordIndex = insertHelper.getColumnIndex(VocabularyMetaData.FOREIGN_WORD);
        int langFlagIndex = insertHelper.getColumnIndex(VocabularyMetaData.LANG_FLAG);

        for (int i = 0; i < data.length; i++) {
            String[] words = TextUtils.split(data[i], WORD_DELIMITER);

            insertHelper.prepareForInsert();

            insertHelper.bind(categoryIdIndex, categoryId);
            insertHelper.bind(translationWordIndex, words[0]);
            insertHelper.bind(foreignWordIndex, words[1]);
            insertHelper.bind(langFlagIndex, language);
            if (categoryId == VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID) { //static main vocabulary
                insertHelper.bind(vocabularyIdIndex, VocabularyMetaData.MAIN_VOCABULARY_ID);
            }

            // Insert the row into the database.
            insertHelper.execute();
        }
    }

    private boolean isDataInitialized(final String language) {
        return sharedPreferences.getBoolean(language, true);
    }
}
