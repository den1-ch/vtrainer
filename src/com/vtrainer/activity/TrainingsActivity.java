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
    //TODO #16 add check available count word to study 
    Intent intent;
    switch (v.getId()) {
      case R.id.t_word_translate_1:
        intent = new Intent(this, ForeignTranslateWordTrainingActivity.class);
        break;
      case R.id.t_word_translate_2:
          intent = new Intent(this, NativeTranslateWordTrainingActivity.class);
        break;
      default:
        return;
    }

    startActivity(intent);
  }
  
}
