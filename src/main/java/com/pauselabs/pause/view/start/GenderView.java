package com.pauselabs.pause.view.start;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 1/28/15.
 */
public class GenderView extends RelativeLayout {

    @InjectView(R.id.ob_name)
    public EditText name;
    @InjectView(R.id.male)
    public Button male;
    @InjectView(R.id.female)
    public Button female;

    public GenderView(Context context) {
        super(context);
    }

    public GenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GenderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }
}
