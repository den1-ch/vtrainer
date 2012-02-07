package com.vtrainer.provider;

import java.util.Calendar;
import java.util.HashMap;

import com.vtrainer.activity.R;
import com.vtrainer.logging.Logger;

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
import android.text.TextUtils;

public class VTrainerProvider extends ContentProvider {
  private static final String TAG = "VTrainerProvider";
  
  private static HashMap<String, String> vocabularyProjectionMap;
  private static HashMap<String, String> trainingProjectionMap;

  static {
    vocabularyProjectionMap = new HashMap<String, String>();
    vocabularyProjectionMap.put(VocabularyTableMetaData._ID, VocabularyTableMetaData._ID);
    vocabularyProjectionMap.put(VocabularyTableMetaData.TRANSLATION_WORD_FN, VocabularyTableMetaData.TRANSLATION_WORD_FN);
    vocabularyProjectionMap.put(VocabularyTableMetaData.FOREIGN_WORD_FN, VocabularyTableMetaData.FOREIGN_WORD_FN);
    vocabularyProjectionMap.put(VocabularyTableMetaData.DATE_CREATED_FN, VocabularyTableMetaData.DATE_CREATED_FN);
    vocabularyProjectionMap.put(VocabularyTableMetaData.PROGRESS_FN, VocabularyTableMetaData.PROGRESS_FN);

    trainingProjectionMap = new HashMap<String, String>();
    trainingProjectionMap.put(TrainingTableMetaData._ID, TrainingTableMetaData._ID);
    trainingProjectionMap.put(TrainingTableMetaData.TYPE_FN, TrainingTableMetaData.TYPE_FN);
    trainingProjectionMap.put(TrainingTableMetaData.WORD_ID_FN, TrainingTableMetaData.WORD_ID_FN);
    trainingProjectionMap.put(VocabularyTableMetaData.FOREIGN_WORD_FN, VocabularyTableMetaData.FOREIGN_WORD_FN); //todo check
    trainingProjectionMap.put(VocabularyTableMetaData.TRANSLATION_WORD_FN, VocabularyTableMetaData.TRANSLATION_WORD_FN); //todo check
    trainingProjectionMap.put(TrainingTableMetaData.PROGRESS_FN, TrainingTableMetaData.PROGRESS_FN);
    trainingProjectionMap.put(TrainingTableMetaData.DATE_LAST_STUDY_FN, TrainingTableMetaData.DATE_LAST_STUDY_FN);
  }
  
  //provide a mechanism to identify all uri patterns
  private static final UriMatcher uriMatcher;
  
  private static final int ALL_WORD_COLLECTION_URI_INDICATOR = 1;
  private static final int COUNT_WORD_URI_INDICATOR          = 2;
  private static final int TRAINING_URI_INDICATOR            = 3;
  private static final int TRAINING_COUNT_URI_INDICATOR      = 4;
  
  static {
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI(VTrainerProviderMetaData.AUTHORITY, VocabularyTableMetaData.TABLE_NAME, ALL_WORD_COLLECTION_URI_INDICATOR);
    uriMatcher.addURI(VTrainerProviderMetaData.AUTHORITY, VocabularyTableMetaData.TABLE_NAME + "/#", COUNT_WORD_URI_INDICATOR);

    uriMatcher.addURI(VTrainerProviderMetaData.AUTHORITY, TrainingTableMetaData.TABLE_NAME + "/#", TRAINING_URI_INDICATOR);
    uriMatcher.addURI(VTrainerProviderMetaData.AUTHORITY, TrainingTableMetaData.TABLE_NAME + "/count", TRAINING_COUNT_URI_INDICATOR);
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
      super(context, VTrainerProviderMetaData.DATABASE_NAME, null, VTrainerProviderMetaData.DATABASE_VERSION);
      
      this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      createVocabularyTable(db);
      createTrainingTable(db);
      
      fillVocabularyStaticData(db);
    }
    
    private void createVocabularyTable(SQLiteDatabase db) {
      StringBuilder sb = new StringBuilder();
      sb.append("CREATE TABLE ");
      sb.append(VocabularyTableMetaData.TABLE_NAME);
      sb.append(" ( \n");
      sb.append(VocabularyTableMetaData._ID);
      sb.append(" INTEGER PRIMARY KEY, \n");
      sb.append(VocabularyTableMetaData.TRANSLATION_WORD_FN);
      sb.append(" VARCHAR(50) NOT NULL, \n");
      sb.append(VocabularyTableMetaData.FOREIGN_WORD_FN);
      sb.append(" VARCHAR(50) NOT NULL, \n");
      sb.append(VocabularyTableMetaData.DATE_CREATED_FN);
      sb.append(" INTEGER NOT NULL, \n");
      sb.append(VocabularyTableMetaData.PROGRESS_FN);
      sb.append(" INTEGER NOT NULL);");
      
      Logger.debug(TAG, "Create table:" + VocabularyTableMetaData.TABLE_NAME + ". SQL: \n" + sb.toString());

      db.execSQL(sb.toString());   
    }
    
    private void createTrainingTable(SQLiteDatabase db) {
      StringBuilder sb = new StringBuilder();
      sb.append("CREATE TABLE ");
      sb.append(TrainingTableMetaData.TABLE_NAME);
      sb.append(" ( \n");
      sb.append(TrainingTableMetaData._ID);
      sb.append(" INTEGER PRIMARY KEY, \n");
      sb.append(TrainingTableMetaData.TYPE_FN);
      sb.append(" INTEGER NOT NULL, \n");
      sb.append(TrainingTableMetaData.WORD_ID_FN);
      sb.append(" INTEGER NOT NULL, \n");
      sb.append(TrainingTableMetaData.PROGRESS_FN);
      sb.append(" INTEGER NOT NULL, \n");
      sb.append(TrainingTableMetaData.DATE_LAST_STUDY_FN);
      sb.append(" INTEGER NOT NULL);");
      
      Logger.debug(TAG, "Create table:" + TrainingTableMetaData.TABLE_NAME + ". SQL: \n" + sb.toString());

      db.execSQL(sb.toString());   
    }
    
    private void fillVocabularyStaticData(SQLiteDatabase db) { //TODO update #3
      Logger.debug(TAG, "Fill vocabulary static data.");
      String[] vocabulary = context.getResources().getStringArray(R.array.vocabulary_array);
      long timestamp = Calendar.getInstance().getTimeInMillis();
      ContentValues cv = new ContentValues();
      for(int i = 0; i < vocabulary.length; i++) {
        String word = vocabulary[i];
      
        int index = word.indexOf(WORD_DELIMITER);
        
        cv.put(VocabularyTableMetaData.TRANSLATION_WORD_FN, word.substring(0, index));
        cv.put(VocabularyTableMetaData.FOREIGN_WORD_FN, word.substring(index + 1));
        cv.put(VocabularyTableMetaData.DATE_CREATED_FN, timestamp);
        cv.put(VocabularyTableMetaData.PROGRESS_FN, VocabularyTableMetaData.INITIAL_PROGRESS);

        long wordId = db.insert(VocabularyTableMetaData.TABLE_NAME, null, cv);
        
        fillTrainingData(db, wordId);
      }
    }

    private void fillTrainingData(SQLiteDatabase db, long wordId) {
      addWordToTraining(db, wordId, TrainingTableMetaData.Type.ForeignWordTranslation.getId());
      //TODO add new trainings types
    }
    
    private void addWordToTraining(SQLiteDatabase db, long wordId, int trainingId) {
      Logger.debug(TAG, "Add word to training. Word id: " + wordId + " training id:" + trainingId);
    	
      ContentValues cv = new ContentValues();

      cv.put(TrainingTableMetaData.TYPE_FN, trainingId);
      cv.put(TrainingTableMetaData.WORD_ID_FN, wordId);
      cv.put(TrainingTableMetaData.PROGRESS_FN, 0);
      cv.put(TrainingTableMetaData.DATE_LAST_STUDY_FN, 0);

      db.insert(TrainingTableMetaData.TABLE_NAME, null, cv);      
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Logger.debug(TAG, "Upgrading db from version" + oldVersion + " to " + newVersion); //TODO save user data #1
      
      db.execSQL("DROP TABLE IF EXISTS " + VocabularyTableMetaData.TABLE_NAME);
      db.execSQL("DROP TABLE IF EXISTS " + TrainingTableMetaData.TABLE_NAME);
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
      "SELECT COUNT(*) FROM " + TrainingTableMetaData.TABLE_NAME + " WHERE " + TrainingTableMetaData.TYPE_FN + " = " + type, null); //TODO add where by time 
  }
  
  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    
    String orderBy = null;
    String limit = null;
    switch (uriMatcher.match(uri)) {
      case ALL_WORD_COLLECTION_URI_INDICATOR:
        qb.setTables(VocabularyTableMetaData.TABLE_NAME);
        qb.setProjectionMap(vocabularyProjectionMap);
        orderBy = VocabularyTableMetaData.DEFAULT_SORT_ORDER;
        break;
      case COUNT_WORD_URI_INDICATOR:
        qb.setTables(VocabularyTableMetaData.TABLE_NAME);
        qb.setProjectionMap(vocabularyProjectionMap);
        limit = uri.getPathSegments().get(1);
        orderBy = VocabularyTableMetaData.DEFAULT_SORT_ORDER;
        break;
      case TRAINING_URI_INDICATOR:
        qb.setTables(TrainingTableMetaData.TABLE_NAME +", " + VocabularyTableMetaData.TABLE_NAME);
        qb.setProjectionMap(trainingProjectionMap);
        qb.appendWhere(
          TrainingTableMetaData.TABLE_NAME + "." + TrainingTableMetaData.WORD_ID_FN + " = " + VocabularyTableMetaData.TABLE_NAME 
          + "." + VocabularyTableMetaData._ID + " AND " + TrainingTableMetaData.TYPE_FN + "=" + uri.getPathSegments().get(1));
        orderBy = TrainingTableMetaData.DEFAULT_SORT_ORDER;
        limit = "1";
        break;
      case TRAINING_COUNT_URI_INDICATOR:  
        return getCountWordAvalaibleToTraining(uri.getPathSegments().get(1));
      default:
        String msg = "Unknown URI" + uri;
        Logger.error(TAG, msg, getContext());
        return null;
    }
    
    //if sort order not specified use the default
    if (TextUtils.isEmpty(sortOrder)) {
      orderBy = sortOrder;
    }
    
    Logger.debug(TAG, qb.buildQuery(projection, selection, selectionArgs, null, null, sortOrder, limit));

    //get db and run the query
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy, limit);
    
    //tell the cursor what uri to watch so it knows when its source data changes
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
      case ALL_WORD_COLLECTION_URI_INDICATOR:
        return addNewWord(uri, values);
    	default:
        Logger.error(TAG, "Unknown URI " + uri, getContext());
        return null;
    }
  }
  
  private Uri addNewWord(Uri uri, ContentValues values) {
    if (values.containsKey(VocabularyTableMetaData.TRANSLATION_WORD_FN) == false) {
      throw new SQLException(VocabularyTableMetaData.TRANSLATION_WORD_FN + " is null");
    }
    
    if (values.containsKey(VocabularyTableMetaData.FOREIGN_WORD_FN) == false) {
      throw new SQLException(VocabularyTableMetaData.FOREIGN_WORD_FN + " is null");
    }
 
    values.put(VocabularyTableMetaData.DATE_CREATED_FN, Calendar.getInstance().getTimeInMillis());
    values.put(VocabularyTableMetaData.PROGRESS_FN, VocabularyTableMetaData.INITIAL_PROGRESS);
    
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    long rowId = db.insert(VocabularyTableMetaData.TABLE_NAME, null, values);
    
    if (rowId > 0) {
    	dbHelper.fillTrainingData(db, rowId);
    	
    	Uri insertedUri = ContentUris.withAppendedId(VocabularyTableMetaData.WORDS_URI, rowId);
      
      getContext().getContentResolver().notifyChange(uri, null);
      
      return insertedUri;
    }
    
    return null;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
    // TODO Auto-generated method stub
    return 0;
  }

}
