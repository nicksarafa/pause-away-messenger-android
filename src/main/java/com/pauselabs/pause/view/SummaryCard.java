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

/**
 * Created by Passa on 12/25/14.
 */
public class SummaryCard extends RelativeLayout {

    private TextView senderName;
    private TextView messageText;

    PauseConversation conversation;

    private boolean showingConvo = false;
    public LinearLayout convoHolderView;

    public SummaryCard(Context context) {
        super(context);
    }

    public SummaryCard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SummaryCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        Injector.inject(this);

        senderName = (TextView) findViewById(R.id.summaryMessageSender);
        messageText = (TextView) findViewById(R.id.summaryMessageText);
        convoHolderView = (LinearLayout) findViewById(R.id.convo_holder_view);
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
