package com.vtrainer.activity;

import java.util.Random;

import com.vtrainer.R;
import com.vtrainer.provider.TrainingMetaData;
import com.vtrainer.provider.VocabularyMetaData;
import com.vtrainer.utils.Constans;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public abstract class AbsractTranslateWordTrainingActivity extends Activity {
	private static final int PROPOSAL_WORD_COUNT = 4;
	
	private final String [] PROJECTION  = new String[] { 
	        TrainingMetaData.WORD_ID, VocabularyMetaData.FOREIGN_WORD, VocabularyMetaData.TRANSLATION_WORD, TrainingMetaData.TABLE_NAME + "." + TrainingMetaData.PROGRESS };
	
	private final Uri NEW_TRANINED_WORD_URI = Uri.withAppendedPath(TrainingMetaData.TRAINING_WORD_URI, getTrainingId());

	private int corectWordAnswerPosition;
	
	private TextView tvTrainedWord;
    private TextView tvTrainedWordProgress;
    private RadioGroup radioGroup;
	private RadioButton [] translateWords = new RadioButton[PROPOSAL_WORD_COUNT];
	
	private RadioButton btnSelectedWord;

    private int trainedWordID;
    private int trainedWordProgress;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.translate_word_training);

        tvTrainedWord = (TextView) findViewById(R.id.wt_trained_word);
        tvTrainedWordProgress = (TextView) findViewById(R.id.wt_progress);

        radioGroup = (RadioGroup) findViewById(R.id.wt_radio_group);
        
        translateWords[0] = (RadioButton) findViewById(R.id.wt_word_translate_1);
        translateWords[1] = (RadioButton) findViewById(R.id.wt_word_translate_2);
        translateWords[2] = (RadioButton) findViewById(R.id.wt_word_translate_3);
        translateWords[3] = (RadioButton) findViewById(R.id.wt_word_translate_4);
        
        if (!initData()) {
            setContentView(R.layout.empty);
            showNoWordForStudyDialog();
        }
    }

    protected abstract String getWordAnswerFieldName();
    
    protected abstract String getWordQuestionFieldName();
    
    protected abstract String getTrainingId();

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
        boolean result;
        try {
            cursorTraining = getContentResolver().query(NEW_TRANINED_WORD_URI, PROJECTION, null, null, "RANDOM()");

            result = cursorTraining.moveToFirst();

            if (result) {
                tvTrainedWord.setText(cursorTraining.getString(cursorTraining.getColumnIndex(getWordQuestionFieldName())));
                trainedWordProgress = cursorTraining.getInt(cursorTraining.getColumnIndex(TrainingMetaData.PROGRESS)) + 1;
                tvTrainedWordProgress.setText(Integer.toString(trainedWordProgress - 1));

                trainedWordID = cursorTraining.getInt(cursorTraining.getColumnIndex(TrainingMetaData.WORD_ID));

                translateWords[corectWordAnswerPosition].setText(cursorTraining.getString(cursorTraining.getColumnIndex(getWordAnswerFieldName())));

                initProposalsData();
            }
        } finally {
            cursorTraining.close();
        }

        return result;
    }

    private void initProposalsData() {
        Cursor proposalsCursor = null;
        try {
            Uri uri = Uri.withAppendedPath(VocabularyMetaData.WORDS_URI, Integer.toString(PROPOSAL_WORD_COUNT - 1));

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

    private void generateNewCorectWordAnswerPosition() {
		corectWordAnswerPosition = new Random().nextInt(PROPOSAL_WORD_COUNT);
	}
	
    public void onRadioButtonClick(View view) {
        btnSelectedWord = (RadioButton) view;
    }
	
	public void onNextButtonClick(View view) {
	    if ((btnSelectedWord == null) || !validateAnswer()) {
	        return;
	    }
	    updateTrainedWordInfo();
        Handler handler = new Handler(); 
        handler.postDelayed(new Runnable() { 
             public void run() { 
                 reinitialize();
                 
                 if (!initData()) {
                     showNoWordForStudyDialog();
                 }
             }

        }, 1);
    }

	private void updateTrainedWordInfo() {
	    ContentValues cv = new ContentValues();
	    cv.put(TrainingMetaData.PROGRESS, getTrainedWordProgress());
        
	    String where = TrainingMetaData.TYPE + "=? AND " + TrainingMetaData.WORD_ID + " =?"; 
	    getContentResolver().update(
	        NEW_TRANINED_WORD_URI, cv, where, new String[] {getTrainingId(), Integer.toString(trainedWordID)});
    }
	
    private void reinitialize() {
        btnSelectedWord.setTextColor(Constans.DEFAULT_COLOR);
        btnSelectedWord = null;

        radioGroup.clearCheck();

        for (RadioButton button : translateWords) {
            button.setTextColor(Constans.DEFAULT_COLOR);
        }
    }

    private boolean validateAnswer() {
        boolean result = true;
        if (!translateWords[corectWordAnswerPosition].isChecked()) {
            translateWords[corectWordAnswerPosition].setChecked(true);
            translateWords[corectWordAnswerPosition].setTextColor(Constans.RIGHT_ANSWER_COLOR);

            btnSelectedWord.setTextColor(Constans.ERROR_COLOR);
            setTrainedWordProgress(-2);
            
            result = false;
        }
        
        return result;
    }	
    
    private void setTrainedWordProgress(int progress) {
        this.trainedWordProgress += progress;
    }
    
    private int getTrainedWordProgress() {
        return trainedWordProgress;
    }

    private void showNoWordForStudyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.no_word_for_training);
        
        builder.setPositiveButton(R.string.caption_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(getBaseContext(), TrainingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
