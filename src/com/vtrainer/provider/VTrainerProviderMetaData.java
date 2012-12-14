package com.vtrainer.provider;

import android.net.Uri;

public class VTrainerProviderMetaData {
  public static final String AUTHORITY             = "com.vtrainer.provider.VTrainerProvider";
  public static final Uri BASE_URI = Uri.parse("content://" + VTrainerProviderMetaData.AUTHORITY);
  
  public static final String DATABASE_NAME         = "vtrainer.db";
  public static final int    DATABASE_VERSION      = 8;
  public static final String VOCABULARY_TABLE_NAME = "vocabulary";
}
