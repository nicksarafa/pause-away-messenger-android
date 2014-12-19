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
    private ArrayList<PauseMessage> messagesReceived;
    private ArrayList<PauseMessage> messagesSentFromUser;
    private ArrayList<PauseBounceBackMessage> messagesSentFromPause;
    private String contact;
    private String contactName;

    private  int numberOfStringFiles = 7;
    private StringRandomizer stringRandomizer;

    public PauseConversation(String contact) {
        this.contact = contact;

        messagesReceived = new ArrayList<PauseMessage>();
        messagesSentFromUser = new ArrayList<PauseMessage>();
        messagesSentFromPause = new ArrayList<PauseBounceBackMessage>();

        // attempt to lookup contact name
        this.contactName = getContactName(PauseApplication.getInstance(), contact);

        Date date = new Date();
        initiatedOn = date.getTime(); // TODO This should be taken from message received

        stringRandomizer = new StringRandomizer(PauseApplication.getInstance(), "stringsSilence1.json");
    }

    public void addMessageFromContact(PauseMessage message) {
        messagesReceived.add(message);
    }
    public void addMessageFromUser(PauseMessage message) {
        messagesSentFromUser.add(message);
    }
    public void addMessageFromPause(PauseBounceBackMessage message) {
        messagesSentFromPause.add(message);
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

    public ArrayList<PauseMessage> getMessagesSentFromUser() { return messagesSentFromUser; }

    public ArrayList<PauseBounceBackMessage> getMessagesSentFromPause() { return messagesSentFromPause; }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
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

    public String getStringForBounceBackMessage() {
        String
                silence = "Silence",
                drive = "Drive",
                sleep = "Sleep",
                modeName = "";

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
        String messageText = stringRandomizer.getString();

        Pattern contactNamePattern = Pattern.compile("%contact");
        messageText = contactNamePattern.matcher(messageText).replaceAll(contactName);

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
