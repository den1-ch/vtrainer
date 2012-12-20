package com.vtrainer.dialog;

import com.vtrainer.R;
import com.vtrainer.provider.VocabularyMetaData;
import com.vtrainer.utils.Constans;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

public class AddNewWordDialog extends Dialog {
  private EditText etForeingWord;
  private EditText etTranslationWord;
  private Button btnSave;
  private Button btnCancel;
  
  private OnDataSaveListener dataSaveListener;
  
  public AddNewWordDialog(Context context, OnDataSaveListener dataSaveListener) {
    super(context);
    
    this.dataSaveListener = dataSaveListener;
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.dialog_add_new_word);

    setTitle(R.string.an_title);
    setCancelable(true);
    
    // setImageResource(R.drawable.add_new_word);

    etForeingWord = (EditText) findViewById(R.id.et_foreing_word);
    etTranslationWord = (EditText) findViewById(R.id.et_translation_word);
    
    btnSave = (Button) findViewById(R.id.add_new_word_save);
    btnSave.setOnClickListener(new android.view.View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        if (!checkFieldData(etForeingWord) || !checkFieldData(etTranslationWord)) {
          return;
        }
        
        ContentValues cv = new ContentValues();
        cv.put(VocabularyMetaData.FOREIGN_WORD, etForeingWord.getText().toString());
        cv.put(VocabularyMetaData.TRANSLATION_WORD, etTranslationWord.getText().toString());
        
        if (getContext().getContentResolver().insert(VocabularyMetaData.WORDS_URI, cv) != null) {
          dataSaveListener.saved();
        }        
        
        init();
        dismiss();
      }
    });

    btnCancel = (Button) findViewById(R.id.add_new_word_cancel);
    btnCancel.setOnClickListener(new android.view.View.OnClickListener() {
      
      @Override
      public void onClick(View v) {
        cancel();
      }
    });
  }
  
  private boolean checkFieldData(EditText field) {
    if (field.getText().toString().equals("")) {
      field.setHintTextColor(Constans.REQUERED_FIELD_HINT_COLOR);
      return false;
    }
    return true;
  }
  
  private void init() {
    etForeingWord.getText().clear();
    etForeingWord.setHintTextColor(Constans.FIELD_HINT_COLOR);
    etTranslationWord.getText().clear();
    etTranslationWord.setHintTextColor(Constans.FIELD_HINT_COLOR);
    
    etForeingWord.requestFocus();
  }

  public interface OnDataSaveListener {
    public void saved();
  }
}
