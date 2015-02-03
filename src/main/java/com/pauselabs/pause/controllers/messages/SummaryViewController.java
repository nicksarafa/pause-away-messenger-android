package com.pauselabs.pause.controllers.messages;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.model.Constants;
import com.pauselabs.pause.view.SummaryReceivedCard;
import com.pauselabs.pause.view.SummarySentCard;
import com.pauselabs.pause.view.tabs.SummaryView;
import com.pauselabs.pause.model.PauseConversation;
import com.pauselabs.pause.model.PauseMessage;
import com.pauselabs.pause.view.SummaryConversationCard;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Passa on 12/25/14.
 */
public class SummaryViewController implements AdapterView.OnItemClickListener, RecyclerView.OnItemTouchListener {

    public SummaryView summaryView;


    private ArrayAdapter<SummaryConversationCard> summaryCardArrayAdapter;

    @Inject LayoutInflater inflater;

    public SummaryViewController() {
        Injector.inject(this);

        summaryView = (SummaryView) inflater.inflate(R.layout.summary_view, null);

        summaryCardArrayAdapter = new SummaryCardAdapter(summaryView.getContext(), R.layout.summary_conversation_card);
        summaryView.listView.setAdapter(summaryCardArrayAdapter);
        summaryView.listView.setOnItemClickListener(this);

        summaryView.startPauseButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                PauseApplication.startPauseService(Constants.Session.Creator.CUSTOM);
            }
        });
    }

    public void updateUI() {
        summaryCardArrayAdapter.clear();
        summaryView.noMessages.setVisibility(View.VISIBLE);

        if (PauseApplication.isActiveSession()) {
            ArrayList<PauseConversation> conversations = PauseApplication.getCurrentSession().getConversationsInTimeOrder();
            if (conversations != null) {
                if (conversations.size() > 0)
                    summaryView.noMessages.setVisibility(View.INVISIBLE);

                for (PauseConversation convo : conversations) {
                    SummaryConversationCard newCard = (SummaryConversationCard) inflater.inflate(R.layout.summary_conversation_card, null);
                    newCard.setConversation(convo);

                    summaryCardArrayAdapter.add(newCard);
                }
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SummaryConversationCard summaryConversationCard = (SummaryConversationCard) view;

        if (summaryConversationCard.isShowingConvo()) {
            summaryConversationCard.convoHolderView.removeAllViews();

            summaryConversationCard.setShowingConvo(false);
        } else {
            PauseConversation conversation = summaryConversationCard.getConversation();
            for (PauseMessage message : conversation.getMessages()) {
                View messageCard = null;
                if (message.getType() == Constants.Message.Type.SMS_INCOMING || message.getType() == Constants.Message.Type.PHONE_INCOMING) {
                    SummaryReceivedCard receivedMessageCard = (SummaryReceivedCard) inflater.inflate(R.layout.summary_received_card, null);
                    receivedMessageCard.setMessageText(message.getMessage());

                    messageCard = receivedMessageCard;
                } else if (message.getType() == Constants.Message.Type.SMS_PAUSE_OUTGOING || message.getType() == Constants.Message.Type.SMS_OUTGOING || message.getType() == Constants.Message.Type.PHONE_OUTGOING){
                    SummarySentCard sentMessageCard = (SummarySentCard) inflater.inflate(R.layout.summary_sent_card, null);
                    sentMessageCard.setMessageText(message.getMessage());

                    messageCard = sentMessageCard;
                }
                switch (message.getType()) {
                    case Constants.Message.Type.SMS_INCOMING:


                        break;
                    case Constants.Message.Type.SMS_OUTGOING:


                        break;
                    case Constants.Message.Type.SMS_PAUSE_OUTGOING:


                        break;
                    case Constants.Message.Type.PHONE_INCOMING:


                        break;
                    case Constants.Message.Type.PHONE_OUTGOING:


                        break;
                }

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                params1.bottomMargin = 10;

                messageCard.setLayoutParams(params1);

                summaryConversationCard.convoHolderView.addView(messageCard);
            }

            summaryConversationCard.setShowingConvo(true);
        }

    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    /**
     * Created by Passa on 12/25/14.
     */
    public static class SummaryCardAdapter extends ArrayAdapter<SummaryConversationCard> {

        public SummaryCardAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SummaryConversationCard summaryConversationCard = getItem(position);
            summaryConversationCard.updateMessageText();

            return summaryConversationCard;
        }

    }
}
