package com.vtrainer.provider;

import com.vtrainer.helper.ImportContentHelper;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.TrainingMetaData.Type;
import com.vtrainer.utils.Constants;
import com.vtrainer.utils.Settings;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class VTrainerDatabase {
    private static final String TAG = "VTrainerDB";
    
    public static final String AUTHORITY = "com.vtrainer.provider.VTrainerProvider";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String DATABASE_NAME = "vtrainer.db";
    public static final int DATABASE_VERSION = 18;

    private DatabaseHelper dbHelper;

    public VTrainerDatabase(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private Context context;
        private Settings settings;
        private ImportContentHelper importContentHelper;
        
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, Constants.IS_TEST_MODE ? DATABASE_VERSION * 10 : DATABASE_VERSION);

            this.context = context;
            settings = new Settings(context);
            importContentHelper = new ImportContentHelper(context, getWritableDatabase());
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.setLockingEnabled(false); //tune performance
                
                Logger.debug(TAG, "Create table:" + VocabularyMetaData.TABLE_NAME + ". SQL: \n" + SQLBuilder.getVocabularyTable());
                db.execSQL(SQLBuilder.getVocabularyTable());
                Logger.debug(TAG, "Create table:" + TrainingMetaData.TABLE_NAME + ". SQL: \n" + SQLBuilder.getTrainingTable());
                db.execSQL(SQLBuilder.getTrainingTable());
                Logger.debug(TAG, "Create table:" + StatisticMetaData.TABLE_NAME + ". SQL: \n" + SQLBuilder.getStatisticTable());
                db.execSQL(SQLBuilder.getStatisticTable());
                
               // updateStaticContent();
            } finally {
                db.setLockingEnabled(true);
            }
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Logger.debug(TAG, "Upgrading db from version" + oldVersion + " to " + newVersion); 
            // TODO save user data #1

            db.execSQL("DROP TABLE IF EXISTS " + VocabularyMetaData.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TrainingMetaData.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + StatisticMetaData.TABLE_NAME);
            onCreate(db);
        }
        
        public void updateStaticContent() {
            importContentHelper.fillVocabularyStaticData();
            fillTrainingData(VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID);

            importContentHelper.fillCategoriesData(); //TODO run it in separate thread
        }
        
        private void fillTrainingData(int categoryId) {
            SQLiteDatabase db = getWritableDatabase();
            for (Type type: TrainingMetaData.Type.values()) {
                db.execSQL(SQLBuilder.getAddCategoryToTrainSQL(), new Object[] { type.getId(), categoryId });
            }
        }
    }
    
    public Uri addNewWord(Uri uri, ContentValues values) {
        if (!values.containsKey(VocabularyMetaData.TRANSLATION_WORD)) {
            throw new SQLException(VocabularyMetaData.TRANSLATION_WORD + " is null");
        }

        if (!values.containsKey(VocabularyMetaData.FOREIGN_WORD)) {
            throw new SQLException(VocabularyMetaData.FOREIGN_WORD + " is null");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        values.put(VocabularyMetaData.LANG_FLAG, dbHelper.settings.getTargetLanguage());
        values.put(VocabularyMetaData.LANG_FLAG, VocabularyMetaData.MAIN_VOCABULARY_ID);
        
        long rowId = db.insert(VocabularyMetaData.TABLE_NAME, null, values);

        if (rowId > 0) {
            addWordToTrainings(rowId);

            Uri insertedUri = ContentUris.withAppendedId(VocabularyMetaData.WORDS_URI, rowId);

            dbHelper.context.getContentResolver().notifyChange(uri, null);

            return insertedUri;
        }
        return null;
    }

    public int addWordsToTrain(Uri uri, ContentValues[] values) {
        int result = 0;
        for (ContentValues contentValues : values) {
            result += addWordToTrainings(contentValues.getAsLong(TrainingMetaData.WORD_ID));
        }
        return result;
    }
    
    public Uri addCategoryToTrain(Uri uri, ContentValues values) {
        dbHelper.fillTrainingData(values.getAsInteger(VocabularyMetaData.CATEGOTY_ID));
        
        return null;
    }

    private int addWordToTrainings(long wordId) {
        String where = TrainingMetaData.WORD_ID + " =? AND " + TrainingMetaData.TYPE + " =?";

        Cursor cursor = dbHelper.getReadableDatabase().query(
           TrainingMetaData.TABLE_NAME, new String[] { TrainingMetaData._ID }, where,
           new String[] { Long.toString(wordId), Integer.toString(TrainingMetaData.Type.ForeignWordTranslation.getId())}, null, null, null);
        
        try {
            if (cursor.moveToFirst()) {
                return 0;
            }

            for (Type type : TrainingMetaData.Type.values()) {
                addWordToTraining(wordId, type.getId());
            }
            return 1;
        } finally {
            cursor.close();
        }
    }

    private void addWordToTraining(long wordId, int trainingId) {
        Logger.debug(TAG, "Add word to training. Word id: " + wordId + " training id:" + trainingId);

        ContentValues cv = new ContentValues();

        cv.put(TrainingMetaData.TYPE, trainingId);
        cv.put(TrainingMetaData.WORD_ID, wordId);

        dbHelper.getWritableDatabase().insert(TrainingMetaData.TABLE_NAME, null, cv);
    }

    public int updateTrainingData(ContentValues values, String selection, String[] selectionArgs) {
        if ((values.size() != 1) || !values.containsKey(TrainingMetaData.PROGRESS)) {
            throw new SQLException("Update do not suported. Values: " + values.toString());
        }
        values.put(TrainingMetaData.DATE_LAST_STUDY, System.currentTimeMillis());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(TrainingMetaData.TABLE_NAME, values, selection, selectionArgs);
     }

    public Cursor getWords(String[] projection, String selection, String[] selectionArgs, String sortOrder, String limit) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(VocabularyMetaData.TABLE_NAME);

        return runQuery(projection, selection, selectionArgs, sortOrder, limit, qb);
    }

    public Cursor getWords(String vocabulary_id, String[] projection) {
        String [] selectionArgs = new String[] { vocabulary_id, dbHelper.settings.getTargetLanguage()};
        String selection = VocabularyMetaData.VOCABULARY_ID + " = ? AND " + VocabularyMetaData.LANG_FLAG + " = ?";
        return getWords(projection, selection, selectionArgs, VocabularyMetaData.DEFAULT_SORT_ORDER, null);
    }
    
    private Cursor runQuery(String[] projection, String selection, String[] selectionArgs, String sortOrder, String limit, SQLiteQueryBuilder qb) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        Logger.debug(TAG, qb.buildQuery(projection, selection, selectionArgs, null, null, sortOrder, limit));

        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit);
    }
    
    private void joinVocabulryToTraining(SQLiteQueryBuilder qb) {
        qb.setTables(VocabularyMetaData.TABLE_NAME + " INNER JOIN " + TrainingMetaData.TABLE_NAME + " ON ( "
                + TrainingMetaData.TABLE_NAME + "." + TrainingMetaData.WORD_ID + " = " + VocabularyMetaData.TABLE_NAME
                + "." + VocabularyMetaData._ID + " )");
    }

    public Cursor getTrainingWord(String trainingType, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        joinVocabulryToTraining(qb);

        qb.appendWhere(TrainingMetaData.TYPE + "=" + trainingType + " AND "
                + TrainingMetaData.TABLE_NAME + "." + TrainingMetaData.PROGRESS + " < " + TrainingMetaData.MAX_PROGRESS + " AND "
                + TrainingMetaData.DATE_LAST_STUDY + " < " + (System.currentTimeMillis() - (Constants.IS_TEST_MODE ? 100: TrainingMetaData.TIME_PERIOD_TO_MEMORIZE_WORD)));

        return runQuery(projection, selection, selectionArgs, sortOrder, "1", qb);
    }

    public void updateStaticContent() {
        dbHelper.updateStaticContent();  
    }
}