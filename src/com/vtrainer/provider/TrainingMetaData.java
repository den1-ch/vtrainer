package com.vtrainer.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class TrainingMetaData implements BaseColumns {
  public static final String TABLE_NAME = "training";
  public static final Uri    CONTENT_URI        = Uri.parse("content://" + VTrainerProviderMetaData.AUTHORITY + "/training/#");
  public static final Uri    TRAINING_COUNT_URI = Uri.parse("content://" + VTrainerProviderMetaData.AUTHORITY + "/training/count");

  // columns
  public static final String TYPE             = "type"; // int - enum
  public static final String WORD_ID          = "word_id"; //int
  public static final String PROGRESS         = "progress"; // byte, max 100
  public static final String DATE_LAST_STUDY  = "date_last_study"; //long
  
  public static final int INITIAL_PROGRESS = 0;

  public static final String DEFAULT_SORT_ORDER = DATE_LAST_STUDY + " DESC";

  public static enum Type { 
      ForeignWordTranslation(1); 
      
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
