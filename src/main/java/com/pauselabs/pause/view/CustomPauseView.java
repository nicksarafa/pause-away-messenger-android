package com.pauselabs.pause.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.model.Constants;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 12/23/14.
 */
public class CustomPauseView extends RelativeLayout implements View.OnClickListener {

    private final String TAG = CustomPauseView.class.getSimpleName();

    @Inject
    SharedPreferences prefs;

    @InjectView(R.id.custom_text)
    TextView customTxtView;
    @InjectView(R.id.begin)
    Button beginBtn;

    public CustomPauseView(Context context) {
        super(context);
    }

    public CustomPauseView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomPauseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
        Injector.inject(this);

        String customMessage = prefs.getString(Constants.Pause.CUSTOM_PAUSE_MESSAGE_KEY, "");
        customTxtView.setText(customMessage);

        beginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.begin && !customTxtView.getText().equals("")) {
            prefs.edit().putString(Constants.Pause.CUSTOM_PAUSE_MESSAGE_KEY, customTxtView.getText().toString()).apply();
        }
    }
}
