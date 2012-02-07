package com.vtrainer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TrainingsActivity extends Activity implements OnClickListener {
  private Button btnWordTranslate_1;	
  private Button btnWordTranslate_2;	
	
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.trainings);

    btnWordTranslate_1 = (Button) findViewById(R.id.t_word_translate_1);
    btnWordTranslate_1.setOnClickListener(this);

    btnWordTranslate_2 = (Button) findViewById(R.id.t_word_translate_2);
    btnWordTranslate_2.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    Intent intent;
    switch (v.getId()) {
      case R.id.t_word_translate_1:
        intent = new Intent(this, TranslateWordTrainingActivity.class);
        break;
      case R.id.t_word_translate_2:
        intent = null;
        break;
      default:
        return;
    }

    startActivity(intent);
  }
  
}
