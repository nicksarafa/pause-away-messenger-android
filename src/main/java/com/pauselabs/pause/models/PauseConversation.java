package com.pauselabs.pause.models;



import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import com.pauselabs.pause.PauseApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Conversations are initiated by an incoming message from someone.  Pause bounce back messages
 * are triggered based on Conversation conditions.  Conversations will keep track of messages received
 * until the end of a the current Pause session in which all data will be discarded.
 */
public class PauseConversation implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long initiatedOn;
    private ArrayList<PauseMessage> messagesReceived;
    private String sender;
    private String contactName;
    private Boolean sentPause;
    private Boolean sentSecondPause = false;
    private String type;

    public PauseConversation(String sender) {
        this.sender = sender;
        messagesReceived = new ArrayList<PauseMessage>();
        // attempt to lookup contact name
        this.contactName = getContactName(PauseApplication.getInstance(), sender);

        Date date = new Date();
        initiatedOn = date.getTime(); // TODO This should be taken from message received
    }

    public void addMessage(PauseMessage message) {
        messagesReceived.add(message);
        type = message.getType();
    }

    public Long getInitiatedOn() {
        return initiatedOn;
    }

    public void setInitiatedOn(Long initiatedOn) {
        this.initiatedOn = initiatedOn;
    }

    public ArrayList<PauseMessage> getMessagesReceived() {
        return messagesReceived;
    }

    public void setMessagesReceived(ArrayList<PauseMessage> messagesReceived) {
        this.messagesReceived = messagesReceived;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Boolean getSentPause() {
        return sentPause;
    }

    public void setSentPause(Boolean sentPause) {
        this.sentPause = sentPause;
    }

    public Boolean getSentSecondPause() {
        return sentSecondPause;
    }

    public void setSentSecondPause(Boolean sentSecondPause) {
        this.sentSecondPause = sentSecondPause;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String name) {
        this.contactName = name;
    }

    public PauseMessage getLastMessageReceived() {
        return messagesReceived.get(messagesReceived.size() -1);
    }

    private static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }



}
