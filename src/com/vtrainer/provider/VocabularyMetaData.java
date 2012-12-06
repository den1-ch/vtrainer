package com.vtrainer.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class VocabularyMetaData implements BaseColumns {
  public static final String TABLE_NAME = "vocabulary";
  
  public static final Uri    WORDS_URI  = Uri.withAppendedPath(VTrainerProviderMetaData.BASE_URI, TABLE_NAME);
  public static final Uri    WORD_URI   = Uri.withAppendedPath(VTrainerProviderMetaData.BASE_URI, TABLE_NAME + "/#");
  
  //columns
  public static final String FOREIGN_WORD     = "foreign_word"; // string
  public static final String TRANSLATION_WORD = "translation_word";  // string
  public static final String DATE_CREATED_FN     = "date_created"; // long
  public static final String PROGRESS_FN         = "progress";     // byte, max 100

  public static final int    INITIAL_PROGRESS = 0;
  
  public static final String DEFAULT_SORT_ORDER = DATE_CREATED_FN + " DESC"; 
}