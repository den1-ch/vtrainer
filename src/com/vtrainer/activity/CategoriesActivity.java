package com.vtrainer.activity;

import com.vtrainer.R;
import com.vtrainer.provider.VocabularyMetaData;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CategoriesActivity extends Activity implements OnClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.categories);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra(VocabularyMetaData.CATEGOTY_ID, v.getId());
        intent.putExtra(VocabularyMetaData.CATEGOTY_NAME, ((Button)v).getText());

        startActivity(intent);
    }  
}
