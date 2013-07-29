package com.vtrainer.data;

import java.util.ArrayList;
import java.util.List;

import com.vtrainer.R;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.VocabularyMetaData;

import android.app.Activity;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class MultipleChoiceAdapter extends SimpleCursorAdapter {
    private static final String TAG = "MultipleChoiceAdapter";

    private LayoutInflater layoutInflater;

    private List<Integer> selectedIds;

    public MultipleChoiceAdapter(Activity activity, Cursor cursor, String[] from, int[] to) {
        super(activity, R.layout.two_item_in_line_with_checkbox, cursor, from, to);

        layoutInflater = activity.getLayoutInflater();
        selectedIds = new ArrayList<Integer>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.two_item_in_line_with_checkbox, null);

            holder = new ViewHolder();
            holder.nativeWord = (TextView) convertView.findViewById(R.id.native_word);
            holder.translatedWord = (TextView) convertView.findViewById(R.id.translated_word);

            holder.cbSelectWord = (CheckBox) convertView.findViewById(R.id.cb_select_word);
            holder.cbSelectWord.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton view, boolean isChecked) {
                    int _id = (Integer) view.getTag();
                    if (isChecked) {
                        getSelectedIds().add(_id);
                    } else {
                        if (getSelectedIds().contains(_id)) {
                            getSelectedIds().remove(_id);
                        }
                    }
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        getCursor().moveToPosition(position);

        int _id = getCursor().getInt(getCursor().getColumnIndex(VocabularyMetaData._ID));
        holder.cbSelectWord.setTag(_id);
        
        holder.nativeWord.setText(getCursor().getString(getCursor().getColumnIndex(VocabularyMetaData.NATIVE_WORD)));
        holder.translatedWord.setText(getCursor().getString(getCursor().getColumnIndex(VocabularyMetaData.TRANSLATION_WORD)));
        Logger.debug(TAG, Integer.toString(_id) + " " + holder.translatedWord.getText().toString());
        
        holder.cbSelectWord.setChecked(getSelectedIds().contains(_id));

        return convertView;
    }

    public List<Integer> getSelectedIds() {
        return selectedIds;
    }

    static class ViewHolder {
        protected TextView nativeWord;
        protected TextView translatedWord;
        protected CheckBox cbSelectWord;
    }
}
