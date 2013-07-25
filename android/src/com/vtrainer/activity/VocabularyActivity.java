package com.vtrainer.activity;

import com.vtrainer.R;
import com.vtrainer.dialog.AddNewWordDialog;
import com.vtrainer.dialog.AddNewWordDialog.OnDataSaveListener;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.VocabularyMetaData;

import android.app.Dialog;
import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;

public class VocabularyActivity extends ListActivity {    
    private final int MENU_GROUP_ID = 1;
    
    private final int MENU_ITEM_ADD_NEW_WORD = 1;    
    
    private final Uri VOCABULARY_URI = Uri.withAppendedPath(VocabularyMetaData.VOCABULARY_URI, 
        Integer.toString(VocabularyMetaData.MAIN_VOCABULARY_ID));

    private final String[] COUNM_NAMES = new String[] { 
        VocabularyMetaData.FOREIGN_WORD, VocabularyMetaData.TRANSLATION_WORD };
    
    private final int[] VIEW_IDS = new int[] { R.id.foreign_word, R.id.translated_word };
    private final String[] PROJECTION = new String[] { 
        VocabularyMetaData._ID, VocabularyMetaData.FOREIGN_WORD, VocabularyMetaData.TRANSLATION_WORD };

    private AddNewWordDialog dlgAddNewWord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateData();
    }

    private void updateData() {
        Cursor cursor = getContentResolver().query(VOCABULARY_URI, PROJECTION, null, null, null);

        SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
        if (adapter == null) {
          adapter = new SimpleCursorAdapter(this, R.layout.two_item_in_line, cursor, COUNM_NAMES, VIEW_IDS);
          setListAdapter(adapter);
        } else {
            adapter.getCursor().close();
            adapter.changeCursor(cursor);
        }
    }
  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(MENU_GROUP_ID, MENU_ITEM_ADD_NEW_WORD, Menu.FIRST, R.string.v_mi_add_new_word);

        return super.onCreateOptionsMenu(menu);
    }
  
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
        case MENU_ITEM_ADD_NEW_WORD:
            showAddNewWordDilalog();
            break;
        default:
            Logger.error("VocabularyActivity", "Unknown menu item " + menuItem.getTitle(), getApplicationContext());
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return dlgAddNewWord;
    }
  
    private void showAddNewWordDilalog() {
        if (dlgAddNewWord == null) {
            OnDataSaveListener dataSaveListener = new OnDataSaveListener() {
                @Override
                public void saved() {
                    updateData(); //TODO performance lost
                }
            };

            dlgAddNewWord = new AddNewWordDialog(this, dataSaveListener);
        }
        dlgAddNewWord.show();
    }
}
