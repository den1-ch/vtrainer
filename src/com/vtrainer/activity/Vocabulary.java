package com.vtrainer.activity;

import com.vtrainer.dialog.AddNewWordDialog;
import com.vtrainer.dialog.AddNewWordDialog.OnDataSaveListener;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.VocabularyTableMetaData;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleCursorAdapter;

public class Vocabulary extends Activity {
  private final String [] COUNM_NAMES = new String[] { VocabularyTableMetaData.FOREIGN_WORD, VocabularyTableMetaData.FOREIGN_WORD };
  private final int []    VIEW_IDS    = new int[]    { R.id.foreign_word, R.id.translated_word};
  private final String [] PROJECTION  = new String[] { VocabularyTableMetaData._ID, VocabularyTableMetaData.FOREIGN_WORD, VocabularyTableMetaData.TRANSLATION_WORD };

  private AddNewWordDialog dlgAddNewWord;
  private GridView gv;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.vocabulary);
   
    gv = (GridView) findViewById(R.id.gv_vocabulary);

    updateData();
    
    gv.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      }
    });

  }

  private void updateData() {
    Cursor cur = getContentResolver().query(VocabularyTableMetaData.WORDS_URI, PROJECTION, null, null, null);

    SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.two_item_in_line, cur, COUNM_NAMES, VIEW_IDS);
    
    gv.setAdapter(adapter);
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    
    inflater.inflate(R.menu.vocabulary_menu, menu);
    return true;
  }
  
  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case R.id.add_new_word:
        showAddNewWordDilalog();
        break;
      default:
        if (Logger.isDebugMode()) {
          throw new IllegalArgumentException("Unknown menu item " + menuItem.getTitle());
        }
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
