package com.vtrainer.activity;

import com.vtrainer.R;
import com.vtrainer.dialog.AddNewWordDialog;
import com.vtrainer.dialog.AddNewWordDialog.OnDataSaveListener;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.VocabularyMetaData;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;

public class VocabularyActivity extends Activity {    
    private final int MENU_GROUP_ID = 1;
    
    private final String[] COUNM_NAMES = new String[] { VocabularyMetaData.FOREIGN_WORD, VocabularyMetaData.TRANSLATION_WORD };
    private final int[] VIEW_IDS = new int[] { R.id.foreign_word, R.id.translated_word };
    private final String[] PROJECTION = new String[] { VocabularyMetaData._ID, VocabularyMetaData.FOREIGN_WORD, VocabularyMetaData.TRANSLATION_WORD };

    private AddNewWordDialog dlgAddNewWord;
    private GridView gv;

    private int categoryId;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.vocabulary);
   
    gv = (GridView) findViewById(R.id.gv_vocabulary);

    categoryId = getIntent().getExtras().getInt(VocabularyMetaData.CATEGOTY_ID);
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
    Cursor cur = getContentResolver().query(VocabularyMetaData.WORDS_URI, PROJECTION, 
        VocabularyMetaData.CATEGOTY_ID + " = ?", new String[] { Integer.toString(categoryId) }, null);

    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.two_item_in_line, cur, COUNM_NAMES, VIEW_IDS);
    
    gv.setAdapter(adapter);
  }
  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isMain()) {
            menu.add(MENU_GROUP_ID, Menu.FIRST, Menu.FIRST, R.string.v_mi_add_new_word);
        } else {
            menu.add(MENU_GROUP_ID, Menu.FIRST, Menu.FIRST, "").setTitle("");
        }

        return super.onCreateOptionsMenu(menu);
    }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.add_new_word:
        showAddNewWordDilalog();
        break;
      default:
        Logger.error("VocabularyActivity", "Unknown menu item " + menuItem.getTitle(), getApplicationContext());
        break;
    }
    return true;
  }
  
  @Override
  protected Dialog onCreateDialog(int id)  {
    return dlgAddNewWord;
  }
  
  private void showAddNewWordDilalog() {
    if (dlgAddNewWord == null) {
      OnDataSaveListener dataSaveListener = new OnDataSaveListener() {
        
        @Override
        public void saved() {
          updateData();      
        }
      };
      
      dlgAddNewWord = new AddNewWordDialog(this, dataSaveListener);
    }

    dlgAddNewWord.show();
  }
  
}
