package com.vtrainer.provider;

import java.util.Calendar;

import com.vtrainer.R;
import com.vtrainer.logging.Logger;
import com.vtrainer.utils.Constans;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class VTrainerProvider extends ContentProvider {
  private static final String TAG = "VTrainerProvider";

  //provide a mechanism to identify all uri patterns
  private static final UriMatcher uriMatcher;
  
  private static final int WORDS_URI_INDICATOR          = 1;
  private static final int COUNT_WORD_URI_INDICATOR     = 2;
  private static final int TRAINING_WORD_URI_INDICATOR  = 3;
  private static final int TRAINING_COUNT_URI_INDICATOR = 4;
  
  static {
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI(VTrainerProviderMetaData.AUTHORITY, VocabularyMetaData.WORDS_PATH, WORDS_URI_INDICATOR);
    uriMatcher.addURI(VTrainerProviderMetaData.AUTHORITY, VocabularyMetaData.TABLE_NAME + "/#", COUNT_WORD_URI_INDICATOR);

    uriMatcher.addURI(VTrainerProviderMetaData.AUTHORITY, TrainingMetaData.TRAINING_WORD_PATH, TRAINING_WORD_URI_INDICATOR);
    uriMatcher.addURI(VTrainerProviderMetaData.AUTHORITY, TrainingMetaData.TABLE_NAME + "/count", TRAINING_COUNT_URI_INDICATOR);
  }
  
  private DatabaseHelper dbHelper;
  
  /**
   * Setup/Create Database
   * This class helps open, create, and upgrade the db
   */
  private static class DatabaseHelper extends SQLiteOpenHelper {
    private static final String WORD_DELIMITER = ";"; //TODO move
    
    private Context context;
    
    public DatabaseHelper(Context context) {
      super(context, VTrainerProviderMetaData.DATABASE_NAME, null, Constans.IS_TEST_MODE ? VTrainerProviderMetaData.DATABASE_VERSION * 10 : VTrainerProviderMetaData.DATABASE_VERSION);
      
      this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.debug(TAG, "Create table:" + VocabularyMetaData.TABLE_NAME + ". SQL: \n" + SQLBuilder.getVocabularyTableSQL());
        db.execSQL(SQLBuilder.getVocabularyTableSQL());
        Logger.debug(TAG, "Create table:" + TrainingMetaData.TABLE_NAME + ". SQL: \n" + SQLBuilder.getTrainingTable());
        db.execSQL(SQLBuilder.getTrainingTable());
        
      fillVocabularyStaticData(db);
      fillCategoriesData(db);
    }

    private void fillVocabularyStaticData(SQLiteDatabase db) { //TODO update #3
      Logger.debug(TAG, "Fill vocabulary static data.");
      String[] vocabulary = context.getResources().getStringArray(Constans.IS_TEST_MODE ? R.array.test_vocabulary_array: R.array.vocabulary_array);
      fillVocabularyData(db, vocabulary, VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID, true);
    }
    
    private void fillCategoriesData(SQLiteDatabase db) {
        Logger.debug(TAG, "Fill categories static data.");

        fillVocabularyData(db, context.getResources().getStringArray(R.array.cat_clothes_array), R.array.cat_clothes_array, false);
        fillVocabularyData(db, context.getResources().getStringArray(R.array.cat_traits_array), R.array.cat_traits_array, false);
    }

    private void fillVocabularyData(SQLiteDatabase db, String[] data, int categoryId, boolean isAddToTraining) {
        long timestamp = Calendar.getInstance().getTimeInMillis();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < data.length; i++) {
            String word = data[i];

            int index = word.indexOf(WORD_DELIMITER);

            cv.put(VocabularyMetaData.CATEGOTY_ID, categoryId);
            cv.put(VocabularyMetaData.TRANSLATION_WORD, word.substring(0, index));
            cv.put(VocabularyMetaData.FOREIGN_WORD, word.substring(index + 1));
            cv.put(VocabularyMetaData.DATE_CREATED_FN, timestamp);
            cv.put(VocabularyMetaData.PROGRESS_FN, VocabularyMetaData.INITIAL_PROGRESS);

            long wordId = db.insert(VocabularyMetaData.TABLE_NAME, null, cv);

            if (isAddToTraining) {
                fillTrainingData(db, wordId);
            }
        }
    }
    
    private void fillTrainingData(SQLiteDatabase db, long wordId) {
      addWordToTraining(db, wordId, TrainingMetaData.Type.ForeignWordTranslation.getId());
      addWordToTraining(db, wordId, TrainingMetaData.Type.NativeWordTranslation.getId());
      //TODO add new trainings types
    }
    
    private void addWordToTraining(SQLiteDatabase db, long wordId, int trainingId) {
      Logger.debug(TAG, "Add word to training. Word id: " + wordId + " training id:" + trainingId);
    	
      ContentValues cv = new ContentValues();

      cv.put(TrainingMetaData.TYPE, trainingId);
      cv.put(TrainingMetaData.WORD_ID, wordId);
      cv.put(TrainingMetaData.PROGRESS, 0);
      cv.put(TrainingMetaData.DATE_LAST_STUDY, 0);

      db.insert(TrainingMetaData.TABLE_NAME, null, cv);      
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Logger.debug(TAG, "Upgrading db from version" + oldVersion + " to " + newVersion); //TODO save user data #1
      
      db.execSQL("DROP TABLE IF EXISTS " + VocabularyMetaData.TABLE_NAME);
      db.execSQL("DROP TABLE IF EXISTS " + TrainingMetaData.TABLE_NAME);
      onCreate(db);
    }
    
  }
  
  @Override
  public boolean onCreate() {
    dbHelper = new DatabaseHelper(getContext());
    
    return true;
  }
  
  public Cursor getCountWordAvalaibleToTraining(String type) {
    return dbHelper.getReadableDatabase().rawQuery(
      "SELECT COUNT(*) FROM " + TrainingMetaData.TABLE_NAME + " WHERE " + TrainingMetaData.TYPE + " = " + type, null); //TODO add where by time 
  }
  
  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    
    String limit = null;
    switch (uriMatcher.match(uri)) {
      case WORDS_URI_INDICATOR:
        qb.setTables(VocabularyMetaData.TABLE_NAME);
        break;
      case COUNT_WORD_URI_INDICATOR:
        qb.setTables(VocabularyMetaData.TABLE_NAME);
        limit = uri.getPathSegments().get(1);
        break;
      case TRAINING_WORD_URI_INDICATOR:
        prepareSelectWordsForTrainingQuery(uri, qb);
        break;
      case TRAINING_COUNT_URI_INDICATOR:  
        return getCountWordAvalaibleToTraining(uri.getPathSegments().get(1));
      default:
        String msg = "Unknown URI" + uri;
        Logger.error(TAG, msg, getContext());
        return null;
    }
    
    Logger.debug(TAG, qb.buildQuery(projection, selection, selectionArgs, null, null, sortOrder, limit));

    //get db and run the query
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit);
    
    //tell the cursor what uri to watch so it knows when its source data changes
    cursor.setNotificationUri(getContext().getContentResolver(), uri);
    
    return cursor;
  }

    private void prepareSelectWordsForTrainingQuery(Uri uri, SQLiteQueryBuilder qb) {
        qb.setTables(VocabularyMetaData.TABLE_NAME + " LEFT OUTER JOIN " + TrainingMetaData.TABLE_NAME + " ON ( "
                + TrainingMetaData.TABLE_NAME + "." + TrainingMetaData.WORD_ID + " = " + VocabularyMetaData.TABLE_NAME
                + "." + VocabularyMetaData._ID + " )");

        qb.appendWhere(TrainingMetaData.TYPE + "=" + uri.getPathSegments().get(1) + " AND "
                + TrainingMetaData.DATE_LAST_STUDY + " < " + (System.currentTimeMillis() - 60 * 60 * 1 * 1000));
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
        return addNewWord(uri, values);
    	default:
        Logger.error(TAG, "Unknown URI " + uri, getContext());
        return null;
    }
  }
  
  private Uri addNewWord(Uri uri, ContentValues values) {
    if (!values.containsKey(VocabularyMetaData.TRANSLATION_WORD)) {
      throw new SQLException(VocabularyMetaData.TRANSLATION_WORD + " is null");
    }
    
    if (!values.containsKey(VocabularyMetaData.FOREIGN_WORD)) {
      throw new SQLException(VocabularyMetaData.FOREIGN_WORD + " is null");
    }
 
    values.put(VocabularyMetaData.DATE_CREATED_FN, Calendar.getInstance().getTimeInMillis());
    values.put(VocabularyMetaData.PROGRESS_FN, VocabularyMetaData.INITIAL_PROGRESS);
    
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    long rowId = db.insert(VocabularyMetaData.TABLE_NAME, null, values);
    
    if (rowId > 0) {
    	dbHelper.fillTrainingData(db, rowId);
    	
    	Uri insertedUri = ContentUris.withAppendedId(VocabularyMetaData.WORDS_URI, rowId);
      
        getContext().getContentResolver().notifyChange(uri, null);
      
        return insertedUri;
    }
    
    return null;
  }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
        case TRAINING_WORD_URI_INDICATOR:
            if ((values.size() != 1) || !values.containsKey(TrainingMetaData.DATE_LAST_STUDY)) {
                throw new SQLException("Update do not suported. Values: " + values.toString());
            }
            
            break;
        default:
            Logger.error(TAG, "Unknown URI " + uri, getContext());
            return 0;
        }
      
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count = db.update(TrainingMetaData.TABLE_NAME, values, selection, selectionArgs);
        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return count;
    }
}
