package com.vtrainer.activity.training;

import com.vtrainer.provider.TrainingMetaData;
import com.vtrainer.provider.VocabularyMetaData;

public class NativeTranslateWordTrainingActivity extends AbsractTranslateWordTrainingActivity {
    protected String getWordAnswerFieldName() {
        return VocabularyMetaData.NATIVE_WORD;
    }
	
    protected String getWordQuestionFieldName() {
        return VocabularyMetaData.TRANSLATION_WORD;
    }
    
    protected String getTrainingId() {
        return TrainingMetaData.Type.NativeWordTranslation.getIdAsString();
    }
}
