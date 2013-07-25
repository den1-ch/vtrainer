package com.vtrainer.activity;

import java.util.List;

import com.vtrainer.R;
import com.vtrainer.data.MultipleChoiceAdapter;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.TrainingMetaData;
import com.vtrainer.provider.VocabularyMetaData;
import com.vtrainer.utils.Constants;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class CategoryActivity extends Activity {
    private final int MENU_GROUP_ID = 1;

    private final int MENU_ITEM_ADD_ALL_TO_STUDY = 1;
    private final int MENU_ITEM_SELECT_WORDS_TO_STUDY = 2;

    private final String[] COUNM_NAMES = new String[] { VocabularyMetaData.FOREIGN_WORD, VocabularyMetaData.TRANSLATION_WORD };
    private final int[] VIEW_IDS = new int[] { R.id.foreign_word, R.id.translated_word };
    private final String[] PROJECTION = new String[] { VocabularyMetaData._ID, VocabularyMetaData.FOREIGN_WORD, VocabularyMetaData.TRANSLATION_WORD };

    private int categoryId;
    private boolean isMultiselectMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryId = getIntent().getExtras().getInt(VocabularyMetaData.CATEGOTY_ID);
        isMultiselectMode = getIntent().getExtras().getBoolean(Constants.SELECT_MODE);

        setContentView(R.layout.vocabulary);
        setTitle(getIntent().getExtras().getCharSequence(VocabularyMetaData.CATEGOTY_NAME));

        Cursor cursor = getContentResolver().query(VocabularyMetaData.WORDS_URI, PROJECTION, VocabularyMetaData.CATEGOTY_ID + " = ?",
                new String[] { Integer.toString(categoryId) }, null);

        BaseAdapter adapter;
        if (isMultiselectMode) {
            adapter = new MultipleChoiceAdapter(this, cursor, COUNM_NAMES, VIEW_IDS);
            miltiselectModeInit((MultipleChoiceAdapter) adapter);
        } else {
            adapter = new SimpleCursorAdapter(this, R.layout.two_item_in_line, cursor, COUNM_NAMES, VIEW_IDS);
        }

        // getListView().addHeaderView(search);

        final ListView list = (ListView) findViewById(R.id.word_list);

        list.setAdapter(adapter);
    }

    private void miltiselectModeInit(final MultipleChoiceAdapter adapter) {
        Button buttonOk = (Button) findViewById(R.id.btn_ok);
        buttonOk.setVisibility(Button.VISIBLE);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View paramView) {
                List<Integer> wordIds = adapter.getSelectedIds();
                if (wordIds.size() > 0) {
                    ContentValues[] values = new ContentValues[wordIds.size()];
                    for (int i = 0; i < wordIds.size(); i++) {
                        values[i] = new ContentValues();
                        values[i].put(TrainingMetaData.WORD_ID, wordIds.get(i));
                    }
                    if (getContentResolver().bulkInsert(Uri.withAppendedPath(TrainingMetaData.TRAINING_WORD_URI, "0"), values) > 0) {
                        showSuccessToast();
                    }
                }
                finish();
            }
        });

        Button buttonCancel = (Button) findViewById(R.id.btn_cancel);
        buttonCancel.setVisibility(Button.VISIBLE);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View paramView) {
                finish();
            }
        });
    }

    private void initMultiSelectActivity() {
        Intent intent = new Intent(getBaseContext(), CategoryActivity.class);
        intent.putExtra(VocabularyMetaData.CATEGOTY_ID, categoryId);
        intent.putExtra(VocabularyMetaData.CATEGOTY_NAME, getTitle());
        intent.putExtra(Constants.SELECT_MODE, true);

        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(MENU_GROUP_ID, MENU_ITEM_ADD_ALL_TO_STUDY, Menu.FIRST, R.string.c_mi_add_all_to_study);
        menu.add(MENU_GROUP_ID, MENU_ITEM_SELECT_WORDS_TO_STUDY, Menu.FIRST, R.string.c_mi_select_words_to_study);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
        case MENU_ITEM_ADD_ALL_TO_STUDY:
            showConfirmationDialog();
            break;
        case MENU_ITEM_SELECT_WORDS_TO_STUDY:
            initMultiSelectActivity();
            break;
        default:
            Logger.error("CategoryActivity", "Unknown menu item " + menuItem.getTitle(), getApplicationContext());
        }
        return true;
    }

    private void showSuccessToast() {
        Toast toast = Toast.makeText(getApplicationContext(), R.string.c_toast_words_added, Toast.LENGTH_SHORT);
        toast.show();
    }
    
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmation_dialog);

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ContentValues cv = new ContentValues();
                cv.put(VocabularyMetaData.CATEGOTY_ID, categoryId);
                getContentResolver().insert(VocabularyMetaData.ADD_CATEGORY_TO_TRAINING_URI, cv);

                showSuccessToast();
                
                Intent intent = new Intent(getBaseContext(), CategoriesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        }).show();
    }
}
