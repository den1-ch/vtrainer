package com.vtrainer.activity;

import com.vtrainer.provider.VocabularyProviderMetaData.VocabularyTableMetaData;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Vocabulary extends ListActivity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
   
    Cursor cur = null;
    String[] vocabulary = null;
    try {
      cur = getContentResolver().query(VocabularyTableMetaData.CONTENT_URI, 
        new String[] {VocabularyTableMetaData.FOREIGN_WORD, VocabularyTableMetaData.NATIVE_WORD}, null, null, null);
      
      if (cur.moveToFirst()) {
        vocabulary = new String[cur.getCount()];
        int counter = 0;
        do {
          String foreignWord = cur.getString(cur.getColumnIndex(VocabularyTableMetaData.FOREIGN_WORD));
          String nativeWord = cur.getString(cur.getColumnIndex(VocabularyTableMetaData.NATIVE_WORD));

          vocabulary[counter++] = foreignWord + " - " + nativeWord; //TODO use format  
        } while (cur.moveToNext());
      }
      
    } finally {
      cur.close();
    }
    
    setListAdapter(new ArrayAdapter<String>(this, R.layout.vocabulary, vocabulary));

    ListView lv = getListView();
    lv.setTextFilterEnabled(true);

    lv.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      }

    });

  }
}
