package com.vtrainer.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class VocabularyMetaData implements BaseColumns {
  public static final String TABLE_NAME = "vocabulary";
  
  public static final String WORDS_PATH = TABLE_NAME;
  public static final String ADD_CATEGORY_TO_TRAINING_PATH = "addCatToTrainings";
  
  public static final Uri WORDS_URI = Uri.withAppendedPath(VTrainerProviderMetaData.BASE_URI, WORDS_PATH);
  public static final Uri WORD_URI  = Uri.withAppendedPath(VTrainerProviderMetaData.BASE_URI, TABLE_NAME + "/#");
  
  public static final Uri ADD_CATEGORY_TO_TRAINING_URI = Uri.withAppendedPath(VTrainerProviderMetaData.BASE_URI, ADD_CATEGORY_TO_TRAINING_PATH);

  //columns
  public static final String CATEGOTY_ID      = "category_id"; // int
  public static final String FOREIGN_WORD     = "foreign_word"; // string
  public static final String TRANSLATION_WORD = "translation_word";  // string
  public static final String DATE_CREATED     = "date_created"; // long
  public static final String PROGRESS         = "progress";     // byte, max 100

  public static final String CATEGOTY_NAME    = "category_name"; // non db column
  
  public static final int INITIAL_PROGRESS = 0;
  
  public static final int MAIN_VOCABULARY_CATEGORY_ID = 1;
  
  public static final String DEFAULT_SORT_ORDER = DATE_CREATED    + " DESC"; 
}