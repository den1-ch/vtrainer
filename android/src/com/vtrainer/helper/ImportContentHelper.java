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
    
    private Context context;
    private Settings settings;
    private SharedPreferences sharedPreferences;
    private SQLiteDatabase db;
    
    public ImportContentHelper(Context context, SQLiteDatabase db) {
        this.context = context;
        this.settings = new Settings(context);
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.db = db;
    }

    public void fillVocabularyStaticData() { //TODO update #3
        InsertHelper insertHelper = new InsertHelper(db, VocabularyMetaData.TABLE_NAME);
        try {
            Logger.debug(TAG, "Fill vocabulary static data.");
            if (settings.getTargetLanguage().equals(Settings.ENG_TARGET_LANGUAGE)) {
                String[] vocabulary = context.getResources().getStringArray(
                    Constants.IS_TEST_MODE ? R.array.test_vocabulary_array : R.array.vocabulary_array);
                fillVocabularyData(insertHelper, vocabulary, VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID);
            } else if (settings.getTargetLanguage().equals(Settings.IT_TARGET_LANGUAGE)) {
                fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.test_vocabulary_array_it),
                    VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID);
            } else {
                Logger.debug(TAG, "Static data for target language:" + settings.getTargetLanguage() + " not avaliable");
            }
        } finally {
            insertHelper.close();
        }
    }
    
    public void fillCategoriesData() {
        Logger.debug(TAG, "Fill categories static data.");
        if (!isDataAvaliable()) {
            return;
        }
        
        InsertHelper insertHelper = new InsertHelper(db, VocabularyMetaData.TABLE_NAME);
        try {
            if (settings.getTargetLanguage().equals(Settings.ENG_TARGET_LANGUAGE)) {
                fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_clothes_array), R.array.cat_clothes_array);
                fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_traits_array), R.array.cat_traits_array);
                fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_sport_array), R.array.cat_sport_array);
                fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_weather_array), R.array.cat_weather_array);
                fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_work_array), R.array.cat_work_array);
                fillVocabularyData(insertHelper, context.getResources().getStringArray(R.array.cat_study_array), R.array.cat_study_array);
            } else {
                Logger.debug(TAG, "Data for target language:" + settings.getTargetLanguage() + " not avaliable");
            }
        } finally {
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(settings.getTargetLanguage(), false);
            editor.commit();
            insertHelper.close();
        }
    }

    private void fillVocabularyData(InsertHelper insertHelper, String[] data, int categoryId) {
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
            insertHelper.bind(langFlagIndex, settings.getTargetLanguage());
            if (categoryId == VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID) { //static main vocabulary
                insertHelper.bind(vocabularyIdIndex, VocabularyMetaData.MAIN_VOCABULARY_ID);
            }

            // Insert the row into the database.
            insertHelper.execute();
        }
    }
    
    private boolean isDataAvaliable() {
        return sharedPreferences.getBoolean(settings.getTargetLanguage(), true);
    }    
}
