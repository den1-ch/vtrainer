package com.vtrainer.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class TrainingTableMetaData implements BaseColumns {
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
  
  public static final String TABLE_NAME = "training";
  public static final Uri    CONTENT_URI        = Uri.parse("content://" + VTrainerProviderMetaData.AUTHORITY + "/training/#");
  public static final Uri    TRAINING_COUNT_URI = Uri.parse("content://" + VTrainerProviderMetaData.AUTHORITY + "/training/count");

  // columns
  public static final String TYPE_FN             = "type"; // int - enum
  public static final String WORD_ID_FN          = "word_id"; //int
  public static final String FOREIGN_WORD_FN     = VocabularyTableMetaData.FOREIGN_WORD_FN;
  public static final String TRANSLATION_WORD_FN = VocabularyTableMetaData.TRANSLATION_WORD_FN;
  public static final String PROGRESS_FN         = "progress"; // byte, max 100
  public static final String DATE_LAST_STUDY_FN  = "date_last_study"; //long
  
  public static final int INITIAL_PROGRESS = 0;

  public static final String DEFAULT_SORT_ORDER = DATE_LAST_STUDY_FN + " DESC";
}
