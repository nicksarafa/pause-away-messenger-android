package com.pauselabs.pause.views;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.models.PauseConversation;

/**
 * Created by Passa on 12/23/14.
 */
public class SummaryButton extends RelativeLayout {

    private RelativeLayout layout;

    private ImageView icon;
    private TextView nameLabel;

    private Image image;
    private String name;

    private PauseConversation conversation;

    public SummaryButton(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.summary_card_view_recieved, this, true);

        nameLabel = (TextView) findViewById(R.id.messageSender);
    }

    public void setName(String name) {
        this.name = name;
        nameLabel.setText(name);
    }

    public void setIcon(Image image) {
        this.image = image;

    }

    public void setConversation(PauseConversation convo) {
        conversation = convo;
    }

    public PauseConversation getConversation() {
        return conversation;
    }

}
