package com.pauselabs.pause.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.pauselabs.R;
import com.pauselabs.pause.models.PauseConversation;
import com.pauselabs.pause.models.PauseMessage;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/** Conversation Adapter is responsible for displaying received conversations in the scoreboard */
public class ConversationAdapter extends BaseAdapter {
  private static final String TAG = ConversationAdapter.class.getSimpleName();

  private ArrayList<PauseConversation> mItems = new ArrayList<PauseConversation>();
  private Context mContext;

  public ConversationAdapter(Context context) {
    mContext = context;
  }

  public int getCount() {
    return mItems.size();
  }

  @Override
  public Object getItem(int position) {
    return mItems.get(position);
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    if (convertView == null) {
      convertView = LayoutInflater.from(mContext).inflate(R.layout.scoreboard_item, parent, false);

      holder = new ViewHolder();
      holder.sourceTypeView = (ImageView) convertView.findViewById(R.id.messageType);
      holder.senderView = (TextView) convertView.findViewById(R.id.messageSender);
      holder.messageView = (TextView) convertView.findViewById(R.id.messageText);
      holder.receivedView = (TextView) convertView.findViewById(R.id.lastMessageReceived);
      //holder.messageCountView = (TextView) convertView.findViewById(R.id.messageCount);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    PauseConversation conversation = (PauseConversation) getItem(position);
    //holder.sourceTypeView.setText(conversation.getType());

    // If sender is a contact, display their name, otherwise display the phone number
    if (conversation.getContactName() != null) {
      holder.senderView.setText(conversation.getContactName());
    } else {
      holder.senderView.setText(conversation.getSender());
    }

    if (conversation.getLastMessageReceived() != null) {
      holder.messageView.setText(conversation.getLastMessageReceived().getText());
    }

    ArrayList<PauseMessage> messages = conversation.getMessagesReceived();

    PauseMessage lastMessagReceived = messages.get(messages.size() - 1);

    SimpleDateFormat df = new SimpleDateFormat("h:mm a");
    Date d = new Date(lastMessagReceived.getReceivedOn());

    holder.receivedView.setText(df.format(d).toString());
    String convoSize = Integer.toString(messages.size());
    //holder.messageCountView.setText(convoSize);

    return convertView;
  }

  public void updateAdapter(ArrayList<PauseConversation> conversations) {
    this.mItems = conversations;
    notifyDataSetChanged();
  }

  private static class ViewHolder {
    private ImageView sourceTypeView;
    private TextView senderView;
    private TextView messageView;
    private TextView receivedView;
    private TextView messageCountView;
  }
}
