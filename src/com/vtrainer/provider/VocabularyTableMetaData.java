package com.vtrainer.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class VocabularyTableMetaData implements BaseColumns {
  public static final String TABLE_NAME         = "vocabulary";
  public static final Uri    CONTENT_URI        = Uri.parse("content://" + VTrainerProviderMetaData.AUTHORITY + "/vocabulary");
  
  //columns
  public static final String NATIVE_WORD        = "native_word";  // string
  public static final String FOREIGN_WORD       = "foreign_word"; // string
  public static final String DATE_CREATED       = "date_created"; // long
  public static final String PROGRESS           = "progress";     // byte, max 100

  public static final String DEFAULT_SORT_ORDER = DATE_CREATED + " DESC"; 
}