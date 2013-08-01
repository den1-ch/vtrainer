package com.vtrainer.activity;

import java.util.Locale;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.vtrainer.R;
import com.vtrainer.activity.training.ForeignTranslateWordTrainingActivity;
import com.vtrainer.activity.training.NativeTranslateWordTrainingActivity;
import com.vtrainer.provider.VocabularyMetaData;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(final int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R. menu.main, menu);
        return true;
    }

    @Override
    public void onTabSelected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(final FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(final int position) {
            Fragment fragment = null;
            switch (position) {
            case 0:
                return new VocabularyActivity();
            case 1:
                return new CategoriesActivity();
            case 2:
                return new TrainingsActivity();
            }

            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.
            fragment = new DummySectionFragment();
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(final int position) {
            switch (position) {
            case 0:
                return getString(R.string.vocabulary_title);
            case 1:
                return getString(R.string.categories_title);
            case 2:
                return getString(R.string.trainings_title);
            }
            return null;
        }

    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Locale locale = new Locale("ua");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
    }

    public void onCategoryClick(final View v) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra(VocabularyMetaData.CATEGOTY_ID, v.getId());
        intent.putExtra(VocabularyMetaData.CATEGOTY_NAME, ((Button)v).getText());

        startActivity(intent);
    }


    public void onTrainingClick(final View v) {
      //TODO #16 add check available count word to study
      Intent intent;
      switch (v.getId()) {
        case R.id.t_word_translate_1:
          intent = new Intent(this, ForeignTranslateWordTrainingActivity.class);
          break;
        case R.id.t_word_translate_2:
            intent = new Intent(this, NativeTranslateWordTrainingActivity.class);
          break;
        default:
          return;
      }

      startActivity(intent);
    }

//    @Override
//    public void onClick(final View v) {
//        Intent intent;
//        switch (v.getId()) {
//        case R.id.vocabulary:
//            intent = new Intent(this, VocabularyActivity.class);
//            break;
//        case R.id.trainings:
//            intent = new Intent(this, TrainingsActivity.class);
//            break;
//        case R.id.categories:
//            intent = new Intent(this, CategoriesActivity.class);
//            break;
//        case R.id.settings:
//            intent = new Intent(this, SettingsActivity.class);
//            break;
//        default:
//            return;
//        }
//
//        startActivity(intent);
//    }
}