package com.vtrainer.data;

import java.util.Calendar;
import java.util.Date;

public class CurrentTrainingStats {
    private Date dateStart;
    
    private int correctAnswerCount;
    private int wrongAnswerCount;
    
    private int avaliableToStudyWordCount;
    
    public CurrentTrainingStats() {
        dateStart = Calendar.getInstance().getTime();
        
        correctAnswerCount = 0;
        wrongAnswerCount = 0;
    }

    public int getCorrectAnswerCount() {
        return correctAnswerCount;
    }

    public void incrementCorrectAnswerCount() {
        correctAnswerCount++;
    }
    
    public int getWrongAnswerCount() {
        return wrongAnswerCount;
    }

    public void incrementWrongAnswerCount() {
        wrongAnswerCount++;
    }

}
