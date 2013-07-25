package com.vtrainer.logging;

import android.content.Context;
import android.util.Log;

public class Logger {
  private static boolean isDebugMode = true;
  
  public static void debug(String tag, String msg) {
    if (isDebugMode) {
      Log.d(tag, msg);
    }
  }

  public static void error(String tag, String msg, Context ctx) {
    if (isDebugMode) {
      Log.e(tag, msg);
      Alert.showAlert(msg, ctx);
    }
  }
}
