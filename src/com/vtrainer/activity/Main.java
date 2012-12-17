package com.vtrainer.activity;

import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
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

        Button btnTrainings = (Button) findViewById(R.id.trainings);
        btnTrainings.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        Locale locale = new Locale("ua");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
        case R.id.vocabulary:
            intent = new Intent(this, VocabularyActivity.class);
            break;
        case R.id.trainings:
            intent = new Intent(this, TrainingsActivity.class);
            break;
        default:
            return;
        }

        startActivity(intent);
    }
}