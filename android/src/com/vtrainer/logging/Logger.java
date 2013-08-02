package com.vtrainer.logging;

import android.content.Context;
import android.util.Log;

public class Logger {
  private static boolean isDebugMode = true;

  public static void debug(final String tag, final String msg) {
    if (isDebugMode) {
      Log.d(tag, msg);
    }
  }

  public static void error(final String tag, final String msg, final Context ctx) { //TODO remove Context ctx
    if (isDebugMode) {
      Log.e(tag, msg);
    }
  }
}
