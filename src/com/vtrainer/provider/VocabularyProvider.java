package com.vtrainer.provider;

import java.util.Calendar;
import java.util.HashMap;

import com.vtrainer.activity.R;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.VocabularyProviderMetaData.VocabularyTableMetaData;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class VocabularyProvider extends ContentProvider {
  private static final String TAG = "VocabularyProvider";
  
  private static HashMap<String, String> vocabularyProjectionMap;

  static {
    vocabularyProjectionMap = new HashMap<String, String>();
    vocabularyProjectionMap.put(VocabularyTableMetaData._ID, VocabularyTableMetaData._ID);
    vocabularyProjectionMap.put(VocabularyTableMetaData.NATIVE_WORD, VocabularyTableMetaData.NATIVE_WORD);
    vocabularyProjectionMap.put(VocabularyTableMetaData.FOREIGN_WORD, VocabularyTableMetaData.FOREIGN_WORD);
    vocabularyProjectionMap.put(VocabularyTableMetaData.DATE_CREATED, VocabularyTableMetaData.DATE_CREATED);
    vocabularyProjectionMap.put(VocabularyTableMetaData.PROGRESS, VocabularyTableMetaData.PROGRESS);
  }
  
  //provide a mechanism to identify all uri patterns
  private static final UriMatcher uriMatcher;
  
  private static final int WORD_COLLECTION_URI_INDICATOR = 1;
  private static final int SINGLE_WORDURI_INDICATOR      = 2;
  static {
    uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    uriMatcher.addURI(VocabularyProviderMetaData.AUTHORITY, VocabularyTableMetaData.TABLE_NAME, WORD_COLLECTION_URI_INDICATOR);
    uriMatcher.addURI(VocabularyProviderMetaData.AUTHORITY, VocabularyTableMetaData.TABLE_NAME + "/#", SINGLE_WORDURI_INDICATOR);
  }
  
  /**
   * Setup/Create Database
   * This class helps open, create, and upgrade the db
   */
  
  private static class DatabaseHelper extends SQLiteOpenHelper {
    private static final String WORD_DELIMITER = ";";
    
    private Context context;
    
    public DatabaseHelper(Context context) {
      super(context, VocabularyProviderMetaData.DATABASE_NAME, null, VocabularyProviderMetaData.DATABASE_VERSION);
      
      this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      StringBuilder sb = new StringBuilder();
      sb.append("CREATE TABLE ");
      sb.append(VocabularyTableMetaData.TABLE_NAME);
      sb.append(" ( \n");
      sb.append(VocabularyTableMetaData._ID);
      sb.append(" INTEGER PRIMARY KEY, \n");
      sb.append(VocabularyTableMetaData.NATIVE_WORD);
      sb.append(" VARCHAR(50), \n");
      sb.append(VocabularyTableMetaData.FOREIGN_WORD);
      sb.append(" VARCHAR(50), \n");
      sb.append(VocabularyTableMetaData.DATE_CREATED);
      sb.append(" INTEGER, \n");
      sb.append(VocabularyTableMetaData.PROGRESS);
      sb.append(" INTEGER );");
      
      Logger.debug(TAG, "Create db. SQL: \n" + sb.toString());

      db.execSQL(sb.toString());   
      
      fillVocabularyData(db);
    }
    
    private void fillVocabularyData(SQLiteDatabase db) { //TODO update #3
      Logger.debug(TAG, "Fill data.");
      String[] vocabulary = context.getResources().getStringArray(R.array.vocabulary_array);
      long timestamp = Calendar.getInstance().getTimeInMillis();
      ContentValues cv = new ContentValues();
      for(int i = 0; i < vocabulary.length; i++) {
        String word = vocabulary[i];
      
        int index = word.indexOf(WORD_DELIMITER);
        
        cv.put(VocabularyTableMetaData.NATIVE_WORD, word.substring(0, index));
        cv.put(VocabularyTableMetaData.FOREIGN_WORD, word.substring(index + 1));
        cv.put(VocabularyTableMetaData.DATE_CREATED, timestamp);
        cv.put(VocabularyTableMetaData.PROGRESS, 0);

        db.insert(VocabularyTableMetaData.TABLE_NAME, null, cv);      
      }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      Logger.debug(TAG, "Upgrading db from version" + oldVersion + " to " + newVersion); //TODO save user data #1
      
      db.execSQL("DROP TABLE IF EXISTS " + VocabularyTableMetaData.TABLE_NAME);
      onCreate(db);
    }
    
  }
  
  private DatabaseHelper dbHelper;
  
  @Override
  public boolean onCreate() {
    dbHelper = new DatabaseHelper(getContext());
    
    return true;
  }
  
  @Override
  public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    
    switch (uriMatcher.match(uri)) {
      case WORD_COLLECTION_URI_INDICATOR:
        qb.setTables(VocabularyTableMetaData.TABLE_NAME);
        qb.setProjectionMap(vocabularyProjectionMap);
        break;
      case SINGLE_WORDURI_INDICATOR:
        qb.setTables(VocabularyTableMetaData.TABLE_NAME);
        qb.setProjectionMap(vocabularyProjectionMap);
        qb.appendWhere(VocabularyTableMetaData._ID + "=" + uri.getPathSegments().get(1));
        break;
      default:
        String msg = "Unknown URI" + uri;
        Logger.error(TAG, msg);
        if (Logger.isDebugMode()) { // TODO implement mechanism for handling such cases on production level #2
          throw new IllegalArgumentException(msg);
        } else {
          return null;
        }
    }
    
    //if sort order not specified use the default
    String orderBy;
    if (TextUtils.isEmpty(sortOrder)) {
      orderBy = VocabularyTableMetaData.DEFAULT_SORT_ORDER;
    } else {
      orderBy = sortOrder;
    }
    
    //get db and run the query
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
    
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int update(Uri uri, ContentValues values, String selection,
      String[] selectionArgs) {
    // TODO Auto-generated method stub
    return 0;
  }

}
