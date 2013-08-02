package com.vtrainer.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.vtrainer.R;
import com.vtrainer.data.MultipleChoiceAdapter;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.TrainingMetaData;
import com.vtrainer.provider.VocabularyMetaData;
import com.vtrainer.utils.Constants;

public class CategoryActivity extends Activity {
    private final int MENU_GROUP_ID = 1;

    private final int MENU_ITEM_ADD_ALL_TO_STUDY = 1;
    private final int MENU_ITEM_SELECT_WORDS_TO_STUDY = 2;

    private final String[] COUNM_NAMES = new String[] { VocabularyMetaData.NATIVE_WORD, VocabularyMetaData.TRANSLATION_WORD };
    private final int[] VIEW_IDS = new int[] { R.id.native_word, R.id.translated_word };
    private final String[] PROJECTION = new String[] { VocabularyMetaData._ID, VocabularyMetaData.NATIVE_WORD, VocabularyMetaData.TRANSLATION_WORD };

    private int categoryId;
    private boolean isMultiselectMode;
    private GestureDetector gestureDetector;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryId = getIntent().getExtras().getInt(VocabularyMetaData.CATEGOTY_ID);
        isMultiselectMode = getIntent().getExtras().getBoolean(Constants.SELECT_MODE);

        setContentView(R.layout.vocabulary);
        setTitle(getIntent().getExtras().getCharSequence(VocabularyMetaData.CATEGOTY_NAME));

        Cursor cursor = getContentResolver().query(VocabularyMetaData.WORDS_URI, PROJECTION, VocabularyMetaData.CATEGOTY_ID + " = ?",
                new String[] { Integer.toString(categoryId) }, null);

        Logger.debug("!!!!!!!!", Integer.toString(cursor.getCount()));
        BaseAdapter adapter;
        if (isMultiselectMode) {
            adapter = new MultipleChoiceAdapter(this, cursor, COUNM_NAMES, VIEW_IDS);
            miltiselectModeInit((MultipleChoiceAdapter) adapter);
        } else {
            adapter = new SimpleCursorAdapter(this, R.layout.two_item_in_line, cursor, COUNM_NAMES, VIEW_IDS);
        }

        gestureDetector = new GestureDetector(getBaseContext(), new GestureListener(cursor));

        // getListView().addHeaderView(search);

        final ListView list = (ListView) findViewById(R.id.word_list);
        list.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        list.setAdapter(adapter);
    }

    private void miltiselectModeInit(final MultipleChoiceAdapter adapter) {
        Button buttonOk = (Button) findViewById(R.id.btn_ok);
        buttonOk.setVisibility(Button.VISIBLE);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View paramView) {
                List<Integer> wordIds = adapter.getSelectedIds();
                if (wordIds.size() > 0) {
                    ContentValues[] values = new ContentValues[wordIds.size()];
                    for (int i = 0; i < wordIds.size(); i++) {
                        values[i] = new ContentValues();
                        values[i].put(TrainingMetaData.WORD_ID, wordIds.get(i));
                    }

                    if (getContentResolver().bulkInsert(Uri.withAppendedPath(TrainingMetaData.TRAINING_WORD_URI, "0"), values) > 0) { //TODO show count of added words
                        showSuccessToast(2);
                    }
                }
                finish();
            }
        });

        Button buttonCancel = (Button) findViewById(R.id.btn_cancel);
        buttonCancel.setVisibility(Button.VISIBLE);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View paramView) {
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
    public boolean onCreateOptionsMenu(final Menu menu) {
        menu.add(MENU_GROUP_ID, MENU_ITEM_ADD_ALL_TO_STUDY, Menu.FIRST, R.string.c_mi_add_all_to_study);
        menu.add(MENU_GROUP_ID, MENU_ITEM_SELECT_WORDS_TO_STUDY, Menu.FIRST, R.string.c_mi_select_words_to_study);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
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

    private void showSuccessToast(final int count) {
        String text = getResources().getQuantityString(R.plurals.c_toast_words_added, count);
        Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmation_dialog);

        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                ContentValues cv = new ContentValues();
                cv.put(VocabularyMetaData.CATEGOTY_ID, categoryId);
                getContentResolver().insert(VocabularyMetaData.ADD_CATEGORY_TO_TRAINING_URI, cv);

                showSuccessToast(2);

                Intent intent = new Intent(getBaseContext(), CategoriesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, final int id) {
                dialog.cancel();
            }
        }).show();
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private final Cursor cursor;

        public GestureListener(final Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public boolean onDown(final MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(final MotionEvent e) {
            if (!isMultiselectMode) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(TrainingMetaData.WORD_ID, cursor.getInt(cursor.getColumnIndex(VocabularyMetaData._ID)));
                if (getContentResolver().insert(Uri.withAppendedPath(TrainingMetaData.TRAINING_WORD_URI, "0"), contentValues) != null) { //TODO update
                    showSuccessToast(1);
                }
            }
            return true;
        }
    }
}
