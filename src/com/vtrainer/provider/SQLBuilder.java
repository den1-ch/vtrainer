package com.vtrainer.provider;

public class SQLBuilder {
    public static String getVocabularyTableSQL() {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ");
        sb.append(VocabularyMetaData.TABLE_NAME);
        sb.append(" ( \n");
        sb.append(VocabularyMetaData._ID);
        sb.append(" INTEGER PRIMARY KEY, \n");
        sb.append(VocabularyMetaData.CATEGOTY_ID);
        sb.append(" INTEGER NOT NULL, \n");
        sb.append(VocabularyMetaData.TRANSLATION_WORD);
        sb.append(" VARCHAR(40) NOT NULL, \n");
        sb.append(VocabularyMetaData.FOREIGN_WORD);
        sb.append(" VARCHAR(40) NOT NULL, \n");
        sb.append(VocabularyMetaData.DATE_CREATED_FN);
        sb.append(" INTEGER NOT NULL, \n");
        sb.append(VocabularyMetaData.PROGRESS_FN);
        sb.append(" INTEGER NOT NULL);");

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
        sb.append(" INTEGER NOT NULL, \n");
        sb.append(TrainingMetaData.DATE_LAST_STUDY);
        sb.append(" INTEGER NOT NULL);");

        return sb.toString();
    }
}
