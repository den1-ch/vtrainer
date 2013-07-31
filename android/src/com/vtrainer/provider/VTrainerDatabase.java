package com.vtrainer.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.vtrainer.helper.ImportContentHelper;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.TrainingMetaData.Type;
import com.vtrainer.utils.Constants;
import com.vtrainer.utils.Settings;

public class VTrainerDatabase {
    private static final String TAG = "VTrainerDB";

    public static final String AUTHORITY = "com.vtrainer.provider.VTrainerProvider";

    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);

    public static final String DATABASE_NAME = "vtrainer.db";
    public static final int DATABASE_VERSION = 19;

    private static Context context;
    private final DatabaseHelper dbHelper;

    public VTrainerDatabase(final Context context) {
        dbHelper = new DatabaseHelper(context);
        this.context = context;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        private final ImportContentHelper importContentHelper;

        public DatabaseHelper(final Context context) {
            super(context, DATABASE_NAME, null, Constants.IS_TEST_MODE ? DATABASE_VERSION * 10 : DATABASE_VERSION);

            importContentHelper = new ImportContentHelper(context);
        }

        @Override
        public void onCreate(final SQLiteDatabase db) {
            try {
                db.setLockingEnabled(false); //tune performance

                Logger.debug(TAG, "Create table:" + VocabularyMetaData.TABLE_NAME + ". SQL: \n" + SQLBuilder.getVocabularyTable());
                db.execSQL(SQLBuilder.getVocabularyTable());
                Logger.debug(TAG, "Create table:" + TrainingMetaData.TABLE_NAME + ". SQL: \n" + SQLBuilder.getTrainingTable());
                db.execSQL(SQLBuilder.getTrainingTable());
                Logger.debug(TAG, "Create table:" + StatisticMetaData.TABLE_NAME + ". SQL: \n" + SQLBuilder.getStatisticTable());
                db.execSQL(SQLBuilder.getStatisticTable());

                updateStaticContent(db, Settings.getTargetLanguage(context));
            } finally {
                db.setLockingEnabled(true);
            }
        }

        @Override
        public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
            Logger.debug(TAG, "Upgrading db from version" + oldVersion + " to " + newVersion);
            // TODO save user data #1

            db.execSQL("DROP TABLE IF EXISTS " + VocabularyMetaData.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + TrainingMetaData.TABLE_NAME);
            db.execSQL("DROP TABLE IF EXISTS " + StatisticMetaData.TABLE_NAME);
            onCreate(db);
        }

        public void updateStaticContent(final SQLiteDatabase db, final String language) {
            new Thread(new Runnable() {  //TODO think about more clever solution
                @Override
                public void run() {
                    importContentHelper.fillVocabularyStaticData(db, language);
                    fillTrainingData(db, VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID);
                }
            }).start();
        }

        private void fillTrainingData(final SQLiteDatabase db, final int categoryId) {
            Logger.debug("", SQLBuilder.getAddCategoryToTrainSQL());
            for (Type type: TrainingMetaData.Type.values()) {
                db.execSQL(SQLBuilder.getAddCategoryToTrainSQL(), new Object[] { type.getId(), categoryId, type.getId() });
            }
        }
    }

    public Uri addNewWord(final Uri uri, final ContentValues values) {
        if (!values.containsKey(VocabularyMetaData.TRANSLATION_WORD)) {
            throw new SQLException(VocabularyMetaData.TRANSLATION_WORD + " is null");
        }

        if (!values.containsKey(VocabularyMetaData.NATIVE_WORD)) {
            throw new SQLException(VocabularyMetaData.NATIVE_WORD + " is null");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        values.put(VocabularyMetaData.LANG_FLAG, Settings.getTargetLanguage(context));
        values.put(VocabularyMetaData.VOCABULARY_ID, Settings.getCurrentVocabularyId());

        long rowId = db.insert(VocabularyMetaData.TABLE_NAME, null, values);

        Uri insertedUri = null;
        if (rowId > 0) {
            addWordToTrain(rowId);

            insertedUri = ContentUris.withAppendedId(VocabularyMetaData.WORDS_URI, rowId);

            context.getContentResolver().notifyChange(uri, null);
        }

        return insertedUri;
    }

    public int addWordsToTrain(final Uri uri, final ContentValues[] values) {
        int result = 0;
        for (ContentValues contentValues : values) {
            result += addWordToTrain(contentValues.getAsLong(TrainingMetaData.WORD_ID));
        }
        return result;
    }

    public Uri addCategoryToTrain(final Uri uri, final ContentValues values) { //TODO update
        dbHelper.fillTrainingData(dbHelper.getWritableDatabase(), values.getAsInteger(VocabularyMetaData.CATEGOTY_ID));

        return null;
    }

    public int addWordToTrain(final Long wordId) {
        if (isWordAlreadyTrained(wordId.toString())) {
            return 0;
        }

        for (Type type : TrainingMetaData.Type.values()) {
            addWordToTraining(wordId, type.getId());
        }
        addWordToCurrentVocabulary(wordId);
        return 1;
    }

    private void addWordToCurrentVocabulary(final Long wordId) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(VocabularyMetaData.VOCABULARY_ID, Settings.getCurrentVocabularyId());

        dbHelper.getWritableDatabase().update(VocabularyMetaData.TABLE_NAME, contentValues,
                VocabularyMetaData._ID + " =?", new String[] { wordId.toString() });
    }

    private boolean isWordAlreadyTrained(final String wordId) {
        String where = TrainingMetaData.WORD_ID + " =? AND " + TrainingMetaData.TYPE + " =?";
        Cursor cursor = dbHelper.getReadableDatabase().query(TrainingMetaData.TABLE_NAME,
                new String[] { TrainingMetaData._ID }, where,
                new String[] { wordId, Integer.toString(TrainingMetaData.Type.ForeignWordTranslation.getId()) }, null,
                null, null);

        boolean result = cursor.moveToFirst();
        cursor.close();

        return result;
    }

    private void addWordToTraining(final long wordId, final int trainingId) {
        Logger.debug(TAG, "Add word to training. Word id: " + wordId + " training id:" + trainingId);

        ContentValues cv = new ContentValues();

        cv.put(TrainingMetaData.TYPE, trainingId);
        cv.put(TrainingMetaData.WORD_ID, wordId);

        dbHelper.getWritableDatabase().insert(TrainingMetaData.TABLE_NAME, null, cv);
    }

    public int updateTrainingData(final ContentValues values, final String selection, final String[] selectionArgs) {
        if ((values.size() != 1) || !values.containsKey(TrainingMetaData.PROGRESS)) {
            throw new SQLException("Update do not suported. Values: " + values.toString());
        }
        values.put(TrainingMetaData.DATE_LAST_STUDY, System.currentTimeMillis());

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.update(TrainingMetaData.TABLE_NAME, values, selection, selectionArgs);
     }

    public Cursor getWords(final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder, final String limit) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(VocabularyMetaData.TABLE_NAME);
        qb.appendWhere(VocabularyMetaData.LANG_FLAG + "= \"" + Settings.getTargetLanguage(context) + "\"");

        return runQuery(projection, selection, selectionArgs, sortOrder, limit, qb);
    }

    public Cursor getWords(final String vocabularyId, final String[] projection) {
        String [] selectionArgs = new String[] { vocabularyId};
        String selection = VocabularyMetaData.VOCABULARY_ID + " = ? ";
        return getWords(projection, selection, selectionArgs, VocabularyMetaData.DEFAULT_SORT_ORDER, null);
    }

    private Cursor runQuery(final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder, final String limit, final SQLiteQueryBuilder qb) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Logger.debug(TAG, qb.buildQuery(projection, selection, selectionArgs, null, null, sortOrder, limit));

        return qb.query(db, projection, selection, selectionArgs, null, null, sortOrder, limit);
    }

    private void joinVocabulryToTraining(final SQLiteQueryBuilder qb) {
        qb.setTables(VocabularyMetaData.TABLE_NAME + " INNER JOIN " + TrainingMetaData.TABLE_NAME + " ON ( "
            + TrainingMetaData.TABLE_NAME + "." + TrainingMetaData.WORD_ID + " = " + VocabularyMetaData.TABLE_NAME
            + "." + VocabularyMetaData._ID + " )");
    }

    public Cursor getTrainingWord(final String trainingType, final String[] projection, final String selection, final String[] selectionArgs, final String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        joinVocabulryToTraining(qb);

        qb.appendWhere(VocabularyMetaData.LANG_FLAG + "= \"" + Settings.getTargetLanguage(context) + "\" AND "
            + TrainingMetaData.TYPE + "=" + trainingType + " AND "
            + TrainingMetaData.TABLE_NAME + "." + TrainingMetaData.PROGRESS + " < " + TrainingMetaData.MAX_PROGRESS + " AND "
            + TrainingMetaData.DATE_LAST_STUDY + " < " + (System.currentTimeMillis() - (Constants.IS_TEST_MODE ? 100: TrainingMetaData.TIME_PERIOD_TO_MEMORIZE_WORD)));

        return runQuery(projection, selection, selectionArgs, sortOrder, "1", qb);
    }

    public void updateStaticContent(final String language) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.updateStaticContent(db, language);
        db.close();
    }
}