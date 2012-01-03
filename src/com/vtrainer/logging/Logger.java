package com.vtrainer.logging;

import android.util.Log;

public class Logger {
  private static boolean isDebugMode = true;
  
  public static boolean isDebugMode() {
    return isDebugMode;
  }

  public static void debug(String tag, String msg) {
    if (isDebugMode) {
      Log.d(tag, msg);
    }
  }

  public static void error(String tag, String msg) {
    Log.e(tag, msg);
  }
}
