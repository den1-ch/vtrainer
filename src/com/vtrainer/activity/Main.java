package com.vtrainer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Main extends Activity implements OnClickListener {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    Button btnVocabulary = (Button) findViewById(R.id.vocabulary);
    btnVocabulary.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.vocabulary:
        Intent intent = new Intent(this, Vocabulary.class);
        startActivity(intent);
      break;
    default:
      break;
    }

    // TODO Auto-generated method stub

  }
}