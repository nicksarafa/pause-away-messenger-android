package com.pauselabs.pause.models;



import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Conversations are initiated by an incoming message from someone.  Pause bounce back messages
 * are triggered based on Conversation conditions.  Conversations will keep track of messages received
 * until the end of a the current Pause session in which all data will be discarded.
 */
public class PauseConversation implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long initiatedOn;
    private ArrayList<PauseMessage> messages;
    private String contactNumber;
    private String contactName;

    private  int numberOfStringFiles = 7;
    private StringRandomizer stringRandomizer;

    public PauseConversation(String contactNumber) {
        this.contactNumber = contactNumber;

        messages = new ArrayList<PauseMessage>();

        // attempt to lookup contact name
        this.contactName = getContactName(PauseApplication.getInstance(), contactNumber);

        Date date = new Date();
        initiatedOn = date.getTime(); // TODO This should be taken from message received

        stringRandomizer = new StringRandomizer(PauseApplication.getInstance(), "stringsSilence1.json");
    }

    public void addMessage(PauseMessage message) {
        messages.add(message);
    }

    public Long getInitiatedOn() {
        return initiatedOn;
    }

    public void setInitiatedOn(Long initiatedOn) {
        this.initiatedOn = initiatedOn;
    }

    public ArrayList<PauseMessage> getMessages() {
        return messages;
    }

    public ArrayList<PauseMessage> getMessagesReceived() {
        ArrayList<PauseMessage> messagesReceived = new ArrayList<PauseMessage>();

        for (int i = 0; i < messages.size(); i++)
            if (messages.get(i).getType() == Constants.Message.Type.SMS_INCOMING)
                messagesReceived.add(messages.get(i));

        return messagesReceived;
    }

    public ArrayList<PauseMessage> getMessagesSentFromUser() {
        ArrayList<PauseMessage> messagesFromUser = new ArrayList<PauseMessage>();

        for (int i = 0; i < messages.size(); i++)
            if (messages.get(i).getType() == Constants.Message.Type.SMS_OUTGOING)
                messagesFromUser.add(messages.get(i));

        return messagesFromUser;
    }

    public ArrayList<PauseMessage> getMessagesSentFromPause() {
        ArrayList<PauseMessage> messagesFromUser = new ArrayList<PauseMessage>();

        for (int i = 0; i < messages.size(); i++)
            if (messages.get(i).getType() == Constants.Message.Type.SMS_PAUSE_OUTGOING)
                messagesFromUser.add(messages.get(i));

        return messagesFromUser;
    }

    public String getContact() {
        return contactNumber;
    }

    public void setContact(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String name) {
        this.contactName = name;
    }

    public PauseMessage getLastMessage() {
        return messages.get(messages.size() - 1);
    }

    public PauseMessage getLastMessageReceived() {
        ArrayList<PauseMessage> messagesReceived = getMessagesReceived();
        return messagesReceived.get(messagesReceived.size() - 1);
    }

    public String getStringForBounceBackMessage() {
        String
                silence = "Silence",
                drive = "Drive",
                sleep = "Sleep",
                modeName = "";


        ArrayList<PauseMessage> messagesReceived = getMessagesReceived();
        int num = (messagesReceived.size() <= numberOfStringFiles) ? messagesReceived.size() : numberOfStringFiles;

        switch (PauseApplication.getCurrentSession().getCreator()) {
            case Constants.Session.Creator.SILENCE:
                modeName = silence;

                break;
            case Constants.Session.Creator.DRIVE:
                modeName = drive;

                break;
            case Constants.Session.Creator.SLEEP:
                modeName = sleep;

                break;
            case Constants.Session.Creator.FLIP:
                modeName = silence;

                break;
        }

        stringRandomizer.setFile("strings" + modeName + num + ".json");
        String messageText = stringRandomizer.getString(contactName);

        return messageText;
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
