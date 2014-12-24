package com.pauselabs.pause.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.core.Constants;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 12/23/14.
 */
public class CustomPauseLayout extends RelativeLayout implements View.OnClickListener {

    private final String TAG = CustomPauseLayout.class.getSimpleName();

    private Context context;

    @Inject
    SharedPreferences prefs;

    @InjectView(R.id.custom_text)
    TextView customTxtView;
    @InjectView(R.id.begin)
    Button beginBtn;

    public CustomPauseLayout(Context context) {
        super(context);

        this.context = context;
    }

    public CustomPauseLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    public CustomPauseLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;
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
