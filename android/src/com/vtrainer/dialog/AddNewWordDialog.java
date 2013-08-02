package com.vtrainer.dialog;

import com.vtrainer.R;
import com.vtrainer.provider.VocabularyMetaData;
import com.vtrainer.utils.Constants;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.support.v4.app.DialogFragment;

public class AddNewWordDialog extends DialogFragment {
  private EditText etNativeWord;
  private EditText etTranslationWord;
  private Button btnSave;
  private Button btnCancel;

  private OnDataSaveListener dataSaveListener;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_add_new_word, container, false);

        setCancelable(true);

        etNativeWord = (EditText) rootView.findViewById(R.id.et_native_word);
        etTranslationWord = (EditText) rootView.findViewById(R.id.et_translation_word);

        btnSave = (Button) rootView.findViewById(R.id.btn_save);
        btnSave.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (!checkFieldData(etNativeWord) || !checkFieldData(etTranslationWord)) {
                    return;
                }

                ContentValues cv = new ContentValues();
                cv.put(VocabularyMetaData.NATIVE_WORD, etNativeWord.getText().toString());
                cv.put(VocabularyMetaData.TRANSLATION_WORD, etTranslationWord.getText().toString());
                cv.put(VocabularyMetaData.CATEGOTY_ID, VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID);

                if (getActivity().getContentResolver().insert(VocabularyMetaData.WORDS_URI, cv) != null) {
                    dataSaveListener.saved();
                }

                init();
                dismiss();
            }
        });

        btnCancel = (Button) rootView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                dismiss();
            }
        });
        return rootView;
    }

  private boolean checkFieldData(final EditText field) {
    if (field.getText().toString().equals("")) {
      field.setHintTextColor(Constants.REQUERED_FIELD_HINT_COLOR);
      return false;
    }
    return true;
  }

  private void init() {
    etNativeWord.getText().clear();
    etNativeWord.setHintTextColor(Constants.FIELD_HINT_COLOR);
    etTranslationWord.getText().clear();
    etTranslationWord.setHintTextColor(Constants.FIELD_HINT_COLOR);

    etNativeWord.requestFocus();
  }

    public void setDataSaveListener(final OnDataSaveListener dataSaveListener) {
        this.dataSaveListener = dataSaveListener;
    }

    public interface OnDataSaveListener {
        public void saved();
    }
}
