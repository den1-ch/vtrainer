package com.vtrainer.activity;

import com.vtrainer.dialog.AddNewWordDialog;
import com.vtrainer.dialog.AddNewWordDialog.OnDataSaveListener;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.VocabularyTableMetaData;

import android.app.Dialog;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Vocabulary extends ListActivity {
  private AddNewWordDialog dlgAddNewWord;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
   
    updateData();
    
    ListView lv = getListView();
    lv.setTextFilterEnabled(true);

    lv.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      }

    });

  }

  private void updateData() {
    Cursor cur = null;
    String[] vocabulary = null;
    try {
      cur = getContentResolver().query(VocabularyTableMetaData.CONTENT_URI, 
        new String[] {VocabularyTableMetaData.FOREIGN_WORD, VocabularyTableMetaData.TRANSLATION_WORD}, null, null, null);
      
      if (cur.moveToFirst()) {
        vocabulary = new String[cur.getCount()];
        int counter = 0;
        do {
          String foreignWord = cur.getString(cur.getColumnIndex(VocabularyTableMetaData.FOREIGN_WORD));
          String nativeWord = cur.getString(cur.getColumnIndex(VocabularyTableMetaData.TRANSLATION_WORD));

          vocabulary[counter++] = foreignWord + " - " + nativeWord; //TODO use format  
        } while (cur.moveToNext());
      }
      
    } finally {
      cur.close();
    }
    
    setListAdapter(new ArrayAdapter<String>(this, R.layout.vocabulary, vocabulary));
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
   // dlgAddNewWord.clear();

    dlgAddNewWord.show();
  }
  
}
