package com.vtrainer.provider;

import android.net.Uri;
import android.provider.BaseColumns;

public class VocabularyProviderMetaData {

  public static final String AUTHORITY                     = "com.vtrainer.provider.VocabularyProvider";
  
  public static final String DATABASE_NAME                 = "vocabulary.db"; //TODO move to more abstract level
  public static final int    DATABASE_VERSION              = 1;
  public static final String NATIVE_LANG_WORDS_TABLE_NAME  = "native_lang_words";
  public static final String FOREIGN_LANG_WORDS_TABLE_NAME = "foreign_lang_words";
  public static final String VOCABULARY_TABLE_NAME         = "vocabulary";
  
  public static final class NativeLangWordsTableMetaData implements BaseColumns {
    public static final String TABLE_NAME  = "native_lang_words";
    public static final Uri    CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/native_lang_words");
    
    //columns
    public static final String WORD        = "word"; //string
  }
  
  public static final class ForeignLangWordsTableMetaData implements BaseColumns {
    public static final String TABLE_NAME  = "foreign_lang_words";
    public static final Uri    CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/foreign_lang_words");
    
    //columns
    public static final String WORD        = "word"; //string
  }
  
  public static final class VocabularyTableMetaData implements BaseColumns {
    public static final String TABLE_NAME   = "vocabulary";
    public static final Uri    CONTENT_URI  = Uri.parse("content://" + AUTHORITY + "/vocabulary");
    
    //columns
    public static final String NATIVE_LANG_ID  = "native_lang_id"; //int
    public static final String FOREIGN_LANG_ID = "foreign_lang_id"; //int
    public static final String PROGRESS        = "progress"; //byte, max 100
  }

  
}
