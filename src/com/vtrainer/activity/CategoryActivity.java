package com.vtrainer.activity;

import com.vtrainer.R;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.VocabularyMetaData;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class CategoryActivity extends Activity {
    private final int MENU_GROUP_ID = 1;

    private final int MENU_ITEM_ADD_ALL_TO_STUDY = 1;

    private final String[] COUNM_NAMES = new String[] { VocabularyMetaData.FOREIGN_WORD, VocabularyMetaData.TRANSLATION_WORD };
    private final int[] VIEW_IDS = new int[] { R.id.foreign_word, R.id.translated_word };
    private final String[] PROJECTION = new String[] { VocabularyMetaData._ID, VocabularyMetaData.FOREIGN_WORD,
            VocabularyMetaData.TRANSLATION_WORD };

    private GridView gv;

    private int categoryId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.vocabulary);

        gv = (GridView) findViewById(R.id.gv_vocabulary);

        categoryId = getIntent().getExtras().getInt(VocabularyMetaData.CATEGOTY_ID);

        setTitle(getIntent().getExtras().getCharSequence(VocabularyMetaData.CATEGOTY_NAME));
        updateData();

        gv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

    }

    private boolean isMain() {
        return (categoryId == VocabularyMetaData.MAIN_VOCABULARY_CATEGORY_ID);
    }

    private void updateData() {
        Cursor cur;
        if (isMain()) {
            cur = getContentResolver().query(VocabularyMetaData.MAIN_VOCABULARY_URI, PROJECTION, null, null, null);
        } else {
            cur = getContentResolver().query(VocabularyMetaData.WORDS_URI, PROJECTION, VocabularyMetaData.CATEGOTY_ID + " = ?",
                    new String[] { Integer.toString(categoryId) }, null);
        }

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.two_item_in_line, cur, COUNM_NAMES, VIEW_IDS);

        gv.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(MENU_GROUP_ID, MENU_ITEM_ADD_ALL_TO_STUDY, Menu.FIRST, R.string.c_mi_add_all_to_study);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
        case MENU_ITEM_ADD_ALL_TO_STUDY:
            showConfirmationDialog();
            break;
        default:
            Logger.error("CategoryActivity", "Unknown menu item " + menuItem.getTitle(), getApplicationContext());
        }
        return true;
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.confirmation_dialog);
        
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ContentValues cv = new ContentValues();
                cv.put(VocabularyMetaData.CATEGOTY_ID, categoryId);
                getContentResolver().insert(VocabularyMetaData.ADD_CATEGORY_TO_TRAINING_URI, cv);
                
                Toast toast = Toast.makeText(getApplicationContext(), R.string.c_toast_words_added, Toast.LENGTH_SHORT);
                toast.show();
                
                Intent intent = new Intent(getBaseContext(), CategoriesActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        }).show();
    }
}
