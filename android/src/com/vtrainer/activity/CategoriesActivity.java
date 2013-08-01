package com.vtrainer.activity;

import com.vtrainer.R;
import com.vtrainer.provider.VocabularyMetaData;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CategoriesActivity extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.categories, container, false);
    }

    public void onCategoryClick(final View v) {
        Intent intent = new Intent(getActivity(), CategoryActivity.class);
        intent.putExtra(VocabularyMetaData.CATEGOTY_ID, v.getId());
        intent.putExtra(VocabularyMetaData.CATEGOTY_NAME, ((Button)v).getText());

        startActivity(intent);
    }
}


