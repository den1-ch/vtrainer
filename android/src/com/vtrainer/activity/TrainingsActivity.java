package com.vtrainer.activity;

import com.vtrainer.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TrainingsActivity extends Fragment {

  @Override
  public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                           final Bundle savedInstanceState) {

      // Inflate the layout for this fragment
      return inflater.inflate(R.layout.trainings, container, false);
  }

}
