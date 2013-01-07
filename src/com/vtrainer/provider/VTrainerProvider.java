package com.vtrainer.provider;

import com.vtrainer.logging.Logger;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class VTrainerProvider extends ContentProvider {
    private static final String TAG = "VTrainerProvider";

    // provide a mechanism to identify all uri patterns
    private static final UriMatcher uriMatcher;

    private static final int WORDS_URI_INDICATOR = 1;
    private static final int PROPOSAL_WORDS_URI_INDICATOR = 2;
    private static final int TRAINING_WORD_URI_INDICATOR = 3;
//    private static final int TRAINING_COUNT_URI_INDICATOR = 4;
    private static final int ADD_CAT_TO_TRAINING_URI_INDICATOR = 5;
    private static final int MAIN_VOCABULARY_URI_INDICATOR = 6;
  
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, VocabularyMetaData.WORDS_PATH, WORDS_URI_INDICATOR);
        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, VocabularyMetaData.PROPOSAL_WORDS_PATH + "/#", PROPOSAL_WORDS_URI_INDICATOR);

        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, TrainingMetaData.TRAINING_WORD_PATH, TRAINING_WORD_URI_INDICATOR);
//        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, TrainingMetaData.TABLE_NAME + "/count", TRAINING_COUNT_URI_INDICATOR);

        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, VocabularyMetaData.ADD_CATEGORY_TO_TRAINING_PATH, ADD_CAT_TO_TRAINING_URI_INDICATOR);
        uriMatcher.addURI(VTrainerDatabase.AUTHORITY, VocabularyMetaData.MAIN_VOCABULARY_PATH, MAIN_VOCABULARY_URI_INDICATOR);
    }

    private VTrainerDatabase vtrainerDatabase;
  
    @Override
    public boolean onCreate() {
        vtrainerDatabase = new VTrainerDatabase(getContext());

        return true;
    }
  
/*
    public Cursor getCountWordAvalaibleToTraining(String type) {
        return dbHelper.getReadableDatabase().rawQuery(
            "SELECT COUNT(*) FROM " + TrainingMetaData.TABLE_NAME + " WHERE " + TrainingMetaData.TYPE + " = " + type, null); //TODO add where by time
        //TODO move SQL to SQLBuilder
    }
*/  
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch (uriMatcher.match(uri)) {
        case WORDS_URI_INDICATOR:
            cursor = vtrainerDatabase.getWords(projection, selection, selectionArgs, sortOrder, null);
            break;
        case PROPOSAL_WORDS_URI_INDICATOR:
            cursor = vtrainerDatabase.getWords(projection, selection, selectionArgs, sortOrder, uri.getPathSegments().get(1));
            break;
        case TRAINING_WORD_URI_INDICATOR:
            cursor = vtrainerDatabase.getTrainingWord(uri.getPathSegments().get(1), projection, selection, selectionArgs, sortOrder);
            break;
//        case TRAINING_COUNT_URI_INDICATOR:
//            return getCountWordAvalaibleToTraining(uri.getPathSegments().get(1));
        case MAIN_VOCABULARY_URI_INDICATOR:
            cursor = vtrainerDatabase.getMainWocabularyWords(projection, selection, selectionArgs, sortOrder);
            break;
        default:
            String msg = "Unknown URI" + uri;
            Logger.error(TAG, msg, getContext());
            return null;
        }
    
        // tell the cursor what uri to watch so it knows when its source data changes
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }
    
    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (uriMatcher.match(uri)) {
        case WORDS_URI_INDICATOR:
            return vtrainerDatabase.addNewWord(uri, values);
        case ADD_CAT_TO_TRAINING_URI_INDICATOR:
            return vtrainerDatabase.addCategoryToTrain(uri, values);
        default:
            Logger.error(TAG, "Unknown URI " + uri, getContext());
            return null;
        }
    }
  
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count;
        switch (uriMatcher.match(uri)) {
        case TRAINING_WORD_URI_INDICATOR:
            count = vtrainerDatabase.updateTrainingData(values, selection, selectionArgs);
            break;
        default:
            Logger.error(TAG, "Unknown URI " + uri, getContext());
            return 0;
        }
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
   }
}