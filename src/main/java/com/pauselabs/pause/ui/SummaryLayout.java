package com.pauselabs.pause.ui;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.pauselabs.R;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.models.PauseConversation;
import com.pauselabs.pause.models.PauseMessage;
import com.pauselabs.pause.views.SettingsButton;
import com.pauselabs.pause.views.SummaryButton;

import java.util.ArrayList;

/**
 * Created by Passa on 12/24/14.
 */
public class SummaryLayout extends LinearLayout implements View.OnClickListener {

    private final String TAG = SummaryLayout.class.getSimpleName();

    private Context context;

    public SummaryLayout(Context context) {
        super(context);

        this.context = context;
    }

    public SummaryLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
    }

    public SummaryLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();


    }

    public void updateSummaryUI() {
        removeAllViews();

        ArrayList<PauseConversation> conversations = PauseApplication.getCurrentSession().getConversationsInTimeOrder();
        if (conversations.size() != 0) {
            for (PauseConversation convo : conversations) {
                SummaryButton newSummaryBtn = new SummaryButton(context);
                newSummaryBtn.setName(convo.getContactName());
                newSummaryBtn.setOnClickListener(this);
                newSummaryBtn.setConversation(convo);

                addView(newSummaryBtn);
            }
        }
    }

    @Override
    public void onClick(View v) {

        if (v instanceof SummaryButton) {
            final SummaryButton btn = (SummaryButton) v;

            if (btn.isShowingConvo()) {
                collapse(btn.convoHolderView);
            } else {
                PauseConversation conversation = btn.getConversation();
                for (PauseMessage message : conversation.getMessages()) {

                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 100);

                    View view = new View(context);
                    view.setBackgroundColor(Color.BLACK);
                    view.setLayoutParams(params1);

                    btn.convoHolderView.addView(view);
                }

                expand(btn.convoHolderView);

                btn.setShowingConvo(true);
            }
        }
    }

    private void expand(LinearLayout ll) {
        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        ll.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(ll, 0, ll.getMeasuredHeight());
        mAnimator.start();
    }

    private void collapse(final LinearLayout ll) {
        int finalHeight = ll.getHeight();

        ValueAnimator mAnimator = slideAnimator(ll, finalHeight, ll.getHeight());
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(final LinearLayout ll, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(1000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = ll.getLayoutParams();
                layoutParams.height = value;
                ll.setLayoutParams(layoutParams);

                Log.i(TAG,"in animation");
            }
        });
        return animator;
    }
}
