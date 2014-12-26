package com.pauselabs.pause.models;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;

import javax.inject.Inject;

import butterknife.InjectView;
import butterknife.Views;

/**
 * Created by Passa on 12/25/14.
 */
public class SummaryCard extends RelativeLayout {

    @Inject
    LayoutInflater inflater;

    private TextView senderName;
    private TextView messageText;

    PauseConversation conversation;

    private boolean showingConvo = false;
    public LinearLayout convoHolderView;

    public SummaryCard(Context context) {
        super(context);

        Injector.inject(this);

        inflater.inflate(R.layout.summary_card_view_conversation, this, true);

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
