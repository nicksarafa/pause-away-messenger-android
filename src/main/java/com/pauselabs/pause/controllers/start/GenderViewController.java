package com.pauselabs.pause.controllers.start;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.activity.StartActivity;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.view.start.GenderView;
import javax.inject.Inject;

/** Created by Passa on 1/28/15. */
public class GenderViewController implements View.OnClickListener {

  private StartActivity startActivity;

  public GenderView genderView;

  String genderValue;

  @Inject LayoutInflater inflater;
  @Inject SharedPreferences prefs;
  @Inject AudioManager am;

  public GenderViewController(StartActivity activity) {
    Injector.inject(this);

    startActivity = activity;

    genderView = (GenderView) inflater.inflate(R.layout.ob_first_gender, null);

    genderView.male.setOnClickListener(this);
    genderView.female.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    if (!genderView.name.getText().toString().equals("")) {
      genderView.male.setEnabled(false);
      genderView.female.setEnabled(false);

      switch (v.getId()) {
        case R.id.male:
          genderValue = Constants.Settings.GENDER_MALE_VALUE;

          break;

        case R.id.female:
          genderValue = Constants.Settings.GENDER_FEMALE_VALUE;

          break;
      }

      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      SharedPreferences.Editor editor = prefs.edit();
      editor.putString(Constants.Settings.NAME_KEY, genderView.name.getText().toString());
      editor.putString(Constants.Settings.GENDER_KEY, genderValue);
      editor.apply();

      genderView.name.onEditorAction(EditorInfo.IME_ACTION_DONE);

      startActivity.showOnboarding();
    } else {
      PauseApplication.sendToast("Please Enter Your Name First!");
    }
  }
}
