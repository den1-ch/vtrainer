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
    
    public ImportContentHelper(Context context) {
        this.context = context;
        this.settings = new Settings(context);
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void fillVocabularyStaticData(SQLiteDatabase db) { //TODO update #3
        Logger.debug(TAG, "Fill vocabulary static data.");
        if (settings.getTargetLanguage().equals(Settings.ENG_TARGET_LANGUAGE)) {
            String[] vocabulary = context.getResources().getStringArray(Constants.IS_TEST_MODE ? R.array.test_vocabulary_array: R.array.vocabulary_array);
            fillVocabularyData(db, vocabulary, VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID);
        } else if (settings.getTargetLanguage().equals(Settings.IT_TARGET_LANGUAGE)) {
            fillVocabularyData(db, context.getResources().getStringArray(R.array.test_vocabulary_array_it), VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID);
        } else {
            Logger.debug(TAG, "Static data for target language:" + settings.getTargetLanguage() + " not avaliable");
        }           
    }
    
    public void fillCategoriesData(SQLiteDatabase db) {
        Logger.debug(TAG, "Fill categories static data.");
        if (!isDataAvaliable()) {
            return;
        }
        
        if (settings.getTargetLanguage().equals(Settings.ENG_TARGET_LANGUAGE)) {
            fillVocabularyData(db, context.getResources().getStringArray(R.array.cat_clothes_array), R.array.cat_clothes_array);
            fillVocabularyData(db, context.getResources().getStringArray(R.array.cat_traits_array), R.array.cat_traits_array);
            fillVocabularyData(db, context.getResources().getStringArray(R.array.cat_sport_array), R.array.cat_sport_array);
            fillVocabularyData(db, context.getResources().getStringArray(R.array.cat_weather_array), R.array.cat_weather_array);
            fillVocabularyData(db, context.getResources().getStringArray(R.array.cat_work_array), R.array.cat_work_array);
            fillVocabularyData(db, context.getResources().getStringArray(R.array.cat_study_array), R.array.cat_study_array);
        } else {
            Logger.debug(TAG, "Data for target language:" + settings.getTargetLanguage() + " not avaliable");
        }
        
        Editor editor = sharedPreferences.edit();
        editor.putBoolean(settings.getTargetLanguage(), false);
        editor.commit();
    }

    private void fillVocabularyData(SQLiteDatabase db, String[] data, int categoryId) {
        InsertHelper insertHelper = new InsertHelper(db, VocabularyMetaData.TABLE_NAME);

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

            // Insert the row into the database.
            insertHelper.execute();
        }
    }
    
    private boolean isDataAvaliable() {
        return sharedPreferences.getBoolean(settings.getTargetLanguage(), true);
    }    
}
