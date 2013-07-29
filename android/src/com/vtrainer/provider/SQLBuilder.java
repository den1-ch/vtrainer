package com.vtrainer.provider;

public class SQLBuilder {
    public static String getVocabularyTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(VocabularyMetaData.TABLE_NAME);
        sb.append(" ( \n");
        sb.append(VocabularyMetaData._ID);
        sb.append(" INTEGER PRIMARY KEY, \n");
        sb.append(VocabularyMetaData.VOCABULARY_ID);
        sb.append(" INTEGER NULL, \n");
        sb.append(VocabularyMetaData.CATEGOTY_ID);
        sb.append(" INTEGER NOT NULL, \n");
        sb.append(VocabularyMetaData.TRANSLATION_WORD);
        sb.append(" VARCHAR(30) NOT NULL, \n");
        sb.append(VocabularyMetaData.NATIVE_WORD);
        sb.append(" VARCHAR(30) NOT NULL, \n");
        sb.append(VocabularyMetaData.DATE_CREATED);
        sb.append(" INTEGER NOT NULL DEFAULT current_timestamp, \n");
        sb.append(VocabularyMetaData.LANG_FLAG);
        sb.append(" VARCHAR(8) NOT NULL, \n");
        sb.append(VocabularyMetaData.PROGRESS);
        sb.append(" INTEGER NOT NULL DEFAULT 0);");

        return sb.toString();
    }

    public static String getTrainingTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(TrainingMetaData.TABLE_NAME);
        sb.append(" ( \n");
        sb.append(TrainingMetaData._ID);
        sb.append(" INTEGER PRIMARY KEY, \n");
        sb.append(TrainingMetaData.TYPE);
        sb.append(" INTEGER NOT NULL, \n");
        sb.append(TrainingMetaData.WORD_ID);
        sb.append(" INTEGER NOT NULL, \n");
        sb.append(TrainingMetaData.PROGRESS);
        sb.append(" INTEGER NOT NULL DEFAULT 0, \n");
        sb.append(TrainingMetaData.DATE_LAST_STUDY);
        sb.append(" INTEGER NOT NULL DEFAULT 0);");

        return sb.toString();
    }
    
    public static String getStatisticTable() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(StatisticMetaData.TABLE_NAME);
        sb.append(" ( \n");
        sb.append(StatisticMetaData._ID);
        sb.append(" INTEGER PRIMARY KEY, \n");
        sb.append(StatisticMetaData.TRAINING_TYPE);
        sb.append(" INTEGER NOT NULL, \n");
        sb.append(StatisticMetaData.DAY);
        sb.append(" INTEGER NOT NULL DEFAULT current_date, \n");
        sb.append(StatisticMetaData.STADIED_COUNT);
        sb.append(" INTEGER NOT NULL, \n");
        sb.append(StatisticMetaData.CORRECT_COUNT);
        sb.append(" INTEGER NOT NULL, \n");
        sb.append(StatisticMetaData.WRONG_COUNT);
        sb.append(" INTEGER NOT NULL);");

        return sb.toString();
    }
    
    public static String getAddCategoryToTrainSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO ");
        sb.append(TrainingMetaData.TABLE_NAME);
        sb.append(" (");
        sb.append(TrainingMetaData.TYPE);
        sb.append(", ");
        sb.append(TrainingMetaData.WORD_ID);
        sb.append(") \n");
        sb.append("SELECT ?, _id \n");
        sb.append("FROM ");
        sb.append(VocabularyMetaData.TABLE_NAME);
        sb.append(" V \n WHERE ");
        sb.append(VocabularyMetaData.CATEGOTY_ID);
        sb.append(" = ? AND NOT EXISTS( \n SELECT * \n FROM ");
        sb.append(TrainingMetaData.TABLE_NAME);
        sb.append(" T \n WHERE T.");
        sb.append(TrainingMetaData.WORD_ID);
        sb.append(" == V._id AND T.");
        sb.append(TrainingMetaData.TYPE);
        sb.append(" == ?)");
        
        return sb.toString();
    }
}
