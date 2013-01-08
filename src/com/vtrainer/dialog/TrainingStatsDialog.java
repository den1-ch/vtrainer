package com.vtrainer.dialog;

import com.vtrainer.R;
import com.vtrainer.activity.TrainingsActivity;
import com.vtrainer.data.CurrentTrainingStats;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TrainingStatsDialog extends Dialog {
    private TextView tvCorrectAnswerCount;
    private TextView tvWrongAnswerCount;
    
    private CurrentTrainingStats trainingStats; 

    public TrainingStatsDialog(Context context, CurrentTrainingStats trainingStats) {
        super(context);
        
        this.trainingStats = trainingStats;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_training_stats);

        setTitle(R.string.no_word_for_training);

        tvCorrectAnswerCount = (TextView) findViewById(R.id.dlg_tr_stats_correct_answer_count);
        tvCorrectAnswerCount.setText(Integer.toString(trainingStats.getCorrectAnswerCount()));
        tvWrongAnswerCount = (TextView) findViewById(R.id.dlg_tr_stats_wrong_answer_count);
        tvWrongAnswerCount.setText(Integer.toString(trainingStats.getWrongAnswerCount()));
        
        Button btnOk = (Button) findViewById(R.id.btn_ok);
        btnOk.setOnClickListener(new android.view.View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent(getContext(), TrainingsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getContext().startActivity(intent);
            }
        });
    }

    public void onOkButtonClick(View v) {
        Intent intent = new Intent(getContext(), TrainingsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getContext().startActivity(intent);
    }
}
