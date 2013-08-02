package com.vtrainer.activity;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import com.vtrainer.R;
import com.vtrainer.dialog.AddNewWordDialog;
import com.vtrainer.dialog.AddNewWordDialog.OnDataSaveListener;
import com.vtrainer.logging.Logger;
import com.vtrainer.provider.VocabularyMetaData;

public class VocabularyActivity extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int VOCABLUARY_LOADER_ID = 0;

    private final int MENU_GROUP_ID = 1;

    private final int MENU_ITEM_ADD_NEW_WORD = 1;

    private final Uri VOCABULARY_URI = Uri.withAppendedPath(VocabularyMetaData.VOCABULARY_URI,
        Integer.toString(VocabularyMetaData.MAIN_VOCABULARY_ID));

    private final String[] COUNM_NAMES = new String[] {
        VocabularyMetaData.NATIVE_WORD, VocabularyMetaData.TRANSLATION_WORD };

    private final int[] VIEW_IDS = new int[] { R.id.native_word, R.id.translated_word };
    private final String[] PROJECTION = new String[] {
        VocabularyMetaData._ID, VocabularyMetaData.NATIVE_WORD, VocabularyMetaData.TRANSLATION_WORD };

    private AddNewWordDialog dlgAddNewWord;
    private SimpleCursorAdapter adapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup viewGroup, final Bundle bundle) {
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(VOCABLUARY_LOADER_ID, null, this);

        adapter = new SimpleCursorAdapter(getActivity(), R.layout.two_item_in_line, null, COUNM_NAMES, VIEW_IDS);
        setListAdapter(adapter);

        return super.onCreateView(inflater, viewGroup, bundle);
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        menu.add(MENU_GROUP_ID, MENU_ITEM_ADD_NEW_WORD, Menu.FIRST, R.string.v_mi_add_new_word);

        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
        case MENU_ITEM_ADD_NEW_WORD:
           // showAddNewWordDilalog();
            break;
        default:
            Logger.error("VocabularyActivity", "Unknown menu item " + menuItem.getTitle(), null);
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(final int loaderId, final Bundle bundle) {
        switch (loaderId) {
        case VOCABLUARY_LOADER_ID:
            return new CursorLoader(getActivity(), VOCABULARY_URI, PROJECTION, null, null, null);
        default:
            return null;
        }
    }

    @Override
    public void onLoadFinished(final Loader<Cursor> cursorLoader, final Cursor cursor) {
        adapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(final Loader<Cursor> cursorLoader) {
        adapter.changeCursor(null);
    }

//    @Override
//    protected Dialog onCreateDialog(final int id) {
//        super.onC
//        return dlgAddNewWord;
//    }
//
//    private void showAddNewWordDilalog() {
//        if (dlgAddNewWord == null) {
//            OnDataSaveListener dataSaveListener = new OnDataSaveListener() {
//                @Override
//                public void saved() {
//                    updateData(); //TODO performance lost
//                }
//            };
//
//            dlgAddNewWord = new AddNewWordDialog(this, dataSaveListener);
//        }
//        dlgAddNewWord.show();
//    }
}
