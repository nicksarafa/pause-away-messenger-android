package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.models.JsonReader;
import com.pauselabs.pause.models.PauseConversation;
import com.pauselabs.pause.util.UIUtils;
import com.pauselabs.pause.views.HomeButton;
import com.pauselabs.pause.views.HomeButtonSeparator;
import com.squareup.otto.Bus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import butterknife.Views;

/**
 * Created by Sarafa on 12/8/14.
 */

public class HomeActivity extends Activity implements View.OnClickListener {

    public static final String TAG = HomeActivity.class.getSimpleName();

    public SettingsLayout settingsLayout;

    RelativeLayout contentLayout;
    LinearLayout buttonLayout;

    @Inject
    Bus mBus;
    @Inject
    SharedPreferences prefs;
    @Inject
    AudioManager am;

    JsonReader jr;

    TextView pauseMessage;

    JSONObject mainObject;
    JSONArray components;
    int count = 0;

    Animation in = new AlphaAnimation(0.0f, 1.0f);
    Animation out = new AlphaAnimation(1.0f, 0.0f);
    AnimationSet as = new AnimationSet(true);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_activity);

        Injector.inject(this);
        Views.inject(this);


        ViewStub stub = (ViewStub) findViewById(R.id.home_view);
        if (PauseApplication.isActiveSession()) {
            stub.setLayoutResource(R.layout.summary_view);
        } else {
            stub.setLayoutResource(R.layout.home_normal);
            contentLayout = (RelativeLayout) stub.inflate();

            LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.home_button_view, (ViewGroup) findViewById(R.id.home_normal), false);
            inflater.inflate(R.layout.home_button_separator, (ViewGroup) findViewById(R.id.home_normal), false);

            buttonLayout = (LinearLayout) findViewById(R.id.home_button_layout);
            pauseMessage = (TextView) findViewById(R.id.home_pause_message);
        }

        settingsLayout = new SettingsLayout(this);

        jr = new JsonReader(this,"jasonBourne.json");
        mainObject = jr.getObject();

        in.setDuration(1000);
        out.setDuration(1000);
        out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                updateView();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        updateView();
    }

    private void updateView() {
        if (PauseApplication.isActiveSession()) {
            updateSummary();
        } else {
            updateNormal();
        }
    }

    private void updateSummary() {
        ArrayList<PauseConversation> conversations = PauseApplication.getCurrentSession().getConversations();
        for (PauseConversation convo : conversations) {
            Log.i(TAG, convo.getContactName());

            ContentResolver cr = getContentResolver();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(convo.getContactNumber()));
            Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
            if(cursor.moveToFirst()) {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                Log.i(TAG, id);
            }

            cursor.close();
        }



    }

    private void updateNormal() {
        buttonLayout.removeAllViews();

        try {
            if (prefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY,false)) {
                components = mainObject.getJSONArray("normalJason");
            } else {
                components = mainObject.getJSONArray("onBoardingProcess");
                count = prefs.getInt(Constants.Pause.ONBOARDING_NUMBER_KEY, 0);
            }

            JSONObject component = (JSONObject)components.get(count);

            String pauseMsg = component.getString("pauseMsg");
            JSONArray btnArray = component.getJSONArray("buttons");

            Pattern contactPattern = Pattern.compile("%name");
            Matcher matcher = contactPattern.matcher(pauseMsg);

            pauseMessage.setText(matcher.replaceAll(prefs.getString(Constants.Settings.NAME_KEY,"")));

            for (int i = 0; i < btnArray.length(); i++) {
                JSONObject btnObject = btnArray.getJSONObject(i);

                HomeButton newBtn = new HomeButton(this);
                newBtn.getButton().setId(btnObject.getInt("actionId"));
                newBtn.getButton().setText(btnObject.getString("btnText"));
                newBtn.getButton().setOnClickListener(this);

                HomeButtonSeparator separator = new HomeButtonSeparator(this);

                buttonLayout.addView(separator);
                buttonLayout.addView(newBtn);
            }

            if (prefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY,false)) {
                HomeButtonSeparator separator = new HomeButtonSeparator(this);
                buttonLayout.addView(separator);

                HomeButton nextBtn = new HomeButton(this);
                nextBtn.getButton().setId(Constants.Settings.ACTION_CYCLE);
                nextBtn.getButton().setText("Next");
                nextBtn.getButton().setOnClickListener(this);

                buttonLayout.addView(nextBtn);
            }

            contentLayout.startAnimation(in);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case Constants.Settings.ACTION_CYCLE:
                cycle();

                break;
            case Constants.Settings.ACTION_ONBOARDING_SILENCE:
                am.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                cycle();

                break;
            case Constants.Settings.ACTION_ONBOARDING_UNSILENCE:
                am.setRingerMode(PauseApplication.getOldRingerMode());

                cycle();

                break;
            case Constants.Settings.ACTION_ONBOARDING_FINISH:
                count = 0;
                prefs.edit().putBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY, true).apply();

                updateView();

                break;
            case Constants.Settings.ACTION_CHANGE_NAME:
                PauseApplication.displayNameDialog(this, settingsLayout.nameBtn);

                break;
            case Constants.Settings.ACTION_CHANGE_GENDER:
                PauseApplication.displayGenderDialog(this, settingsLayout.genderBtn);

                break;
        }
    }

    private void cycle() {
        if (!prefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY,false))
            prefs.edit().putInt(Constants.Pause.ONBOARDING_NUMBER_KEY, count + 1).apply();

        count = (count < components.length() - 1) ? ++count : 0;

        contentLayout.startAnimation(out);
    }

    private boolean isTablet() {
        return UIUtils.isTablet(this);
    }
}
