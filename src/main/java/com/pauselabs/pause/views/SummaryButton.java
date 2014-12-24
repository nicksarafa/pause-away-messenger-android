package com.pauselabs.pause.views;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pauselabs.R;
import com.pauselabs.pause.models.PauseConversation;

import org.w3c.dom.Text;

/**
 * Created by Passa on 12/23/14.
 */
public class SummaryButton extends RelativeLayout {

    private RelativeLayout layout;

    private ImageView icon;
    private TextView nameLabel;

    private Image image;
    private String name;

    private boolean showingConvo = false;
    public LinearLayout convoHolderView;

    private PauseConversation conversation;

    public SummaryButton(Context context) {
        super(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.conversation_button_view, this, true);

        nameLabel = (TextView) findViewById(R.id.name_label);
        convoHolderView = (LinearLayout) findViewById(R.id.convo_holder_view);
    }

    public void setName(String name) {
        this.name = name;
        nameLabel.setText(name);
    }

    public void setIcon(Image image) {
        this.image = image;

    }

    public boolean isShowingConvo() {
        return showingConvo;
    }

    public void setShowingConvo(boolean showing) {
        showingConvo = showing;
    }

    public void setConversation(PauseConversation convo) {
        conversation = convo;
    }

    public PauseConversation getConversation() {
        return conversation;
    }

}
