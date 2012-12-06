package com.vtrainer.activity;

import java.util.Random;

import com.vtrainer.provider.TrainingMetaData;
import com.vtrainer.provider.VocabularyMetaData;
import com.vtrainer.utils.Constans;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

public class TranslateWordTrainingActivity extends Activity {
	private static final int TRANSLATED_WORD_COUNT = 4;
	
	private final String [] PROJECTION  = new String[] { VocabularyMetaData.FOREIGN_WORD, VocabularyMetaData.TRANSLATION_WORD };
	
	private final Uri NEW_TRANINED_WORD_URI = Uri.withAppendedPath(TrainingMetaData.CONTENT_URI, TrainingMetaData.Type.ForeignWordTranslation.getIdAsString());

	private int corectWordAnswerPosition;
	
	private TextView tvTrainedWord;
	private RadioButton [] translateWords = new RadioButton[TRANSLATED_WORD_COUNT];
	
	private RadioButton btnSelectedWord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // if (initData()) {
        // } else {
        //
        // }
        setContentView(R.layout.translate_word_training);

        tvTrainedWord = (TextView) findViewById(R.id.wt_trained_word);
    
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
            cursorTraining = getContentResolver().query(NEW_TRANINED_WORD_URI, PROJECTION, null, null, "RANDOM()");

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
            Uri uri = Uri.withAppendedPath(VocabularyMetaData.WORDS_URI, Integer.toString(TRANSLATED_WORD_COUNT - 1));

            String orderBy = "RANDOM()";
            String where = getWordAnswerFieldName() + "!= \"" + translateWords[corectWordAnswerPosition].getText().toString() + "\"";
            proposalsCursor = getContentResolver().query(uri, new String[] { getWordAnswerFieldName() }, where, null, orderBy);

            proposalsCursor.moveToFirst();

            int index = -1;
            int fieldIndex = proposalsCursor.getColumnIndex(getWordAnswerFieldName());
            do {
                if (++index == corectWordAnswerPosition) {
                    index++;
                }

                translateWords[index].setText(proposalsCursor.getString(fieldIndex)); // TODO
            } while (proposalsCursor.moveToNext());

        } finally {
            proposalsCursor.close();
        }
    }

    protected String getWordAnswerFieldName() {
        return VocabularyMetaData.TRANSLATION_WORD;
    }
	
	private void generateNewCorectWordAnswerPosition() {
		corectWordAnswerPosition = new Random().nextInt(TRANSLATED_WORD_COUNT);
	}
	
    public void onRadioButtonClick(View view) {
        btnSelectedWord = (RadioButton) view;
    }
	
	public void onNextButtonClick(View view) {
        validateAnswer();
        
        Handler handler = new Handler(); 
        handler.postDelayed(new Runnable() { 
             public void run() { 
                 reinitialize();
                 
                 if (!initData()) {
                     // TODO
                 }
             }

        }, 1000);        
        
    }

    private void reinitialize() {
        if (btnSelectedWord != null) {
             btnSelectedWord.setTextColor(Constans.DEFAULT_COLOR);
             btnSelectedWord = null;
         }                 
         translateWords[corectWordAnswerPosition].setChecked(false);
    } 

    private void validateAnswer() {
        if (!translateWords[corectWordAnswerPosition].isChecked()) {
            translateWords[corectWordAnswerPosition].setChecked(true);
            if (btnSelectedWord != null) {
                btnSelectedWord.setTextColor(Constans.ERROR_COLOR);
            }
        }
    }
	
}
