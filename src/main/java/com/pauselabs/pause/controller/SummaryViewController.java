package com.pauselabs.pause.controller;

import android.content.Context;
import android.graphics.Color;
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
import com.pauselabs.pause.view.SummaryView;
import com.pauselabs.pause.model.PauseConversation;
import com.pauselabs.pause.model.PauseMessage;
import com.pauselabs.pause.view.SummaryCard;

import java.util.ArrayList;

import javax.inject.Inject;

/**
 * Created by Passa on 12/25/14.
 */
public class SummaryViewController implements AdapterView.OnItemClickListener, RecyclerView.OnItemTouchListener {

    public SummaryView summaryView;

    private ArrayAdapter<SummaryCard> summaryCardArrayAdapter;

    @Inject LayoutInflater inflater;

    public SummaryViewController() {
        Injector.inject(this);

        summaryView = (SummaryView) inflater.inflate(R.layout.summary_view, null);

        summaryCardArrayAdapter = new SummaryCardAdapter(summaryView.getContext(), R.layout.summary_card_view_conversation);
        summaryView.listView.setOnItemClickListener(this);
        summaryView.listView.setAdapter(summaryCardArrayAdapter);
    }

    public void updateUI() {
        ArrayList<PauseConversation> conversations = PauseApplication.getCurrentSession().getConversationsInTimeOrder();
        if (conversations.size() > 0)
            summaryView.noMessages.setVisibility(View.INVISIBLE);
        else
            summaryView.noMessages.setVisibility(View.VISIBLE);

        summaryCardArrayAdapter.clear();
        for (PauseConversation convo : conversations) {
            SummaryCard newCard = (SummaryCard) inflater.inflate(R.layout.summary_card_view_conversation, null);
            newCard.setConversation(convo);

            summaryCardArrayAdapter.add(newCard);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SummaryCard summaryCard = (SummaryCard) view;

        if (summaryCard.isShowingConvo()) {
            summaryCard.convoHolderView.removeAllViews();

            summaryCard.setShowingConvo(false);
        } else {
            PauseConversation conversation = summaryCard.getConversation();
            for (PauseMessage message : conversation.getMessages()) {

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);
                params1.bottomMargin = 10;

                View newView = new View(summaryCard.getContext());
                newView.setBackgroundColor(Color.BLACK);
                newView.setLayoutParams(params1);

                summaryCard.convoHolderView.addView(newView);
            }

            summaryCard.setShowingConvo(true);
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
    public static class SummaryCardAdapter extends ArrayAdapter<SummaryCard> {

        public SummaryCardAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SummaryCard summaryCard = getItem(position);
            summaryCard.updateMessageText();

            return summaryCard;
        }

    }
}
