package com.vtrainer.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class TrainingMetaData implements BaseColumns {
  public static final String TABLE_NAME = "training";
  
  public static final String TRAINING_WORD_PATH = TABLE_NAME + "/#";

  public static final Uri TRAINING_WORD_URI  = Uri.withAppendedPath(VTrainerDatabase.BASE_URI, TABLE_NAME);
  public static final Uri TRAINING_COUNT_URI = Uri.withAppendedPath(VTrainerDatabase.BASE_URI, "/training/count");
  
  // columns
  public static final String TYPE            = "type"; // int - enum
  public static final String WORD_ID         = "word_id"; //int
  public static final String PROGRESS        = "progress"; // byte, max 100
  public static final String DATE_LAST_STUDY = "date_last_study"; //long
  
  public static final int INITIAL_PROGRESS = 0;
  public static final int MAX_PROGRESS = 4;

  public static final String DEFAULT_SORT_ORDER = DATE_LAST_STUDY + " DESC";
  
  public static final int TIME_PERIOD_TO_MEMORIZE_WORD = 60 * 60 * 1 * 1000;
  
  public static enum Type { 
      ForeignWordTranslation(1), NativeWordTranslation(2); 
      
      private int id;
    
      Type(int id) {
        this.id = id;
      }
      
      public int getId() {
        return id;
      }
      
      public String getIdAsString() {
        return Integer.toString(id);
      }
    }; 
}
