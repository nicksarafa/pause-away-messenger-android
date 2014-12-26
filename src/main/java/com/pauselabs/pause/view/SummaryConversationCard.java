package com.pauselabs.pause.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.model.PauseConversation;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 12/25/14.
 */
public class SummaryConversationCard extends RelativeLayout {

    @InjectView(R.id.summaryMessageSender)
    TextView senderName;
    @InjectView(R.id.summaryMessageText)
    TextView messageText;
    @InjectView(R.id.convo_holder_view)
    public LinearLayout convoHolderView;

    PauseConversation conversation;

    private boolean showingConvo = false;

    public SummaryConversationCard(Context context) {
        super(context);
    }

    public SummaryConversationCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SummaryConversationCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Injector.inject(this);
        Views.inject(this);
    }

    public void setConversation(PauseConversation conversation) {
        this.conversation = conversation;

        senderName.setText(conversation.getContactName());
        updateMessageText();
    }

    public PauseConversation getConversation() {
        return conversation;
    }

    public void updateMessageText() {
        Log.i("SummaryCard",conversation.getLastMessageReceived().toString());
        if (messageText != null)
        messageText.setText(conversation.getLastMessageReceived().getMessage());
    }

    public boolean isShowingConvo() {
        return showingConvo;
    }

    public void setShowingConvo(boolean showing) {
        showingConvo = showing;
    }


}