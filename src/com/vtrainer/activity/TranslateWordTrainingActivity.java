package com.vtrainer.activity;

import com.vtrainer.provider.TrainingTableMetaData;
import com.vtrainer.provider.VocabularyTableMetaData;

import android.app.Activity;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class TranslateWordTrainingActivity extends Activity {
	private static final int TRANSLATED_WORD_COUNT = 4;
	
	private final String [] PROJECTION  = new String[] { TrainingTableMetaData.FOREIGN_WORD_FN, TrainingTableMetaData.TRANSLATION_WORD_FN };
	
	private final Uri NEW_TRANINED_WORD_URI = Uri.withAppendedPath(TrainingTableMetaData.CONTENT_URI, TrainingTableMetaData.Type.ForeignWordTranslation.getIdAsString());

	private int corectWordAnswerPosition;
	
	private TextView tvTrainedWord;
	private RadioGroup radioGroup;
	private RadioButton [] translateWords = new RadioButton[TRANSLATED_WORD_COUNT];
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
//    if (initData()) {
//    } else {
//
//    }
    setContentView(R.layout.translate_word_training);
    tvTrainedWord = (TextView) findViewById(R.id.wt_trained_word);
    radioGroup = (RadioGroup) findViewById(R.id.wt_radio_group);
    translateWords[0] = (RadioButton) findViewById(R.id.wt_word_translate_1);
    translateWords[1] = (RadioButton) findViewById(R.id.wt_word_translate_2);
    translateWords[2] = (RadioButton) findViewById(R.id.wt_word_translate_3);
    translateWords[3] = (RadioButton) findViewById(R.id.wt_word_translate_4);

    initData();
	}

//	private int getCountWordToTrain() {
//    Cursor countCursor = null;
//    try {
//      Uri uri = Uri.withAppendedPath(VocabularyTableMetaData.WORDS_URI, Integer.toString(TRANSLATED_WORD_COUNT)); // TODO must be updated; proposal data can not be // static must be random
//      countCursor = getContentResolver().query(uri, new String[] { getWordAnswerFieldName() }, null, null, null);
//
//      countCursor.moveToFirst();
//
//      translateWords[index].setText(proposalsCursor.getString(proposalsCursor.getColumnIndex(getWordAnswerFieldName())));
//    } finally {
//      countCursor.close();
//    }
//	}
	
	private boolean initData() {
    generateNewCorectWordAnswerPosition();

    Cursor cursorTraining = null;
    try {
      cursorTraining = getContentResolver().query(NEW_TRANINED_WORD_URI, PROJECTION, null, null, null);
    	
      if (!cursorTraining.moveToFirst()) {
        return false; 
      }
      
      tvTrainedWord.setText(cursorTraining.getString(cursorTraining.getColumnIndex(PROJECTION[0])));
      translateWords[corectWordAnswerPosition].setText(cursorTraining.getString(cursorTraining.getColumnIndex(PROJECTION[1])));
    } finally {
      cursorTraining.close();
    }
    initProposalsData();

    return true;
	}
	
	private void initProposalsData() {
    Cursor proposalsCursor = null;
    try {
      Uri uri = Uri.withAppendedPath(VocabularyTableMetaData.WORDS_URI, Integer.toString(TRANSLATED_WORD_COUNT - 1)); // TODO must be updated; proposal data can not be // static must be random
      proposalsCursor = getContentResolver().query(uri, new String[] { getWordAnswerFieldName() }, null, null, null);

      proposalsCursor.moveToFirst();

      int index = -1;
      int fieldIndex = proposalsCursor.getColumnIndex(getWordAnswerFieldName());
      do {
        if (++index == corectWordAnswerPosition) {
          index++;
        }

        translateWords[index].setText(proposalsCursor.getString(fieldIndex)); //TODO
      } while (proposalsCursor.moveToNext());

    } finally {
      proposalsCursor.close();
    }
	}

	protected String getWordAnswerFieldName() {
	  return VocabularyTableMetaData.TRANSLATION_WORD_FN;
	}
	
	private void generateNewCorectWordAnswerPosition() {
		corectWordAnswerPosition = 1; //TODO
	}
	
	public void onNextButtonClick(View view){
    generateNewCorectWordAnswerPosition();
    if (!initData()) {
      // TODO
    }
	}
	
}
