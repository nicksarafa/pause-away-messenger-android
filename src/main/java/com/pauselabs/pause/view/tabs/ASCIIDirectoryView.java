package com.pauselabs.pause.view.tabs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.joanzapata.android.iconify.IconDrawable;
import com.joanzapata.android.iconify.Iconify;
import com.pauselabs.R;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 1/27/15.
 */
public class ASCIIDirectoryView extends RelativeLayout {

    @InjectView(R.id.emojiDirectoryGrid)
    public ListView asciiGrid;
    @InjectView(R.id.custom_text)
    public EditText customText;

    //Adding the fa-pencil doesn't work. I'm not sure why

    public EditText getCustomText() {

        Drawable pencilIcon = new IconDrawable(getContext(), Iconify.IconValue.fa_pencil).colorRes(R.color.pause_dark_grey).actionBarSize();
        getCustomText().setCompoundDrawables(pencilIcon, null, null, null);
        getCustomText().setCompoundDrawablePadding(4);

        return customText;
    }

    public ASCIIDirectoryView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ASCIIDirectoryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public ASCIIDirectoryView(Context context) {
        super(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Views.inject(this);
    }

}
