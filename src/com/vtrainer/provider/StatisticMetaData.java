package com.vtrainer.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class StatisticMetaData implements BaseColumns {
  public static final String TABLE_NAME = "statistic";
  
  public static final String STATISTIC_PATH = TABLE_NAME;
  
  public static final Uri STATISTIC_URI  = Uri.withAppendedPath(VTrainerProviderMetaData.BASE_URI, STATISTIC_PATH);
  
  // columns
  public static final String TRAINING_TYPE = "training_type"; // int - enum
  public static final String DAY           = "day"; //long
  public static final String STADIED_COUNT = "stadied_count"; //int
  public static final String WRONG_COUNT   = "wrong_count"; //int
  public static final String CORRECT_COUNT = "correct_count"; //int

  public static final String DEFAULT_SORT_ORDER = DAY + " DESC";
}
