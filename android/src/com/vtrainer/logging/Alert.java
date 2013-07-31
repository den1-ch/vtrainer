package com.vtrainer.logging;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class Alert {

  public static void showAlert(String msg, Context ctx) {
    Builder builder = new Builder(ctx);
    
    builder.setTitle("Error");
    builder.setMessage(msg);
    builder.setPositiveButton("Ok", new OnClickListener() {
      public void onClick(DialogInterface dialog, int which) {
        
      }
    });
    
    builder.create().show();
  }
}
