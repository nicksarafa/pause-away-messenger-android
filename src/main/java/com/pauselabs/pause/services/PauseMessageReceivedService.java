package com.pauselabs.pause.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.events.PauseMessageReceivedEvent;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.pauselabs.pause.models.PauseConversation;
import com.pauselabs.pause.models.PauseMessage;
import com.pauselabs.pause.models.PauseSession;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * When a Broadcast Receiver (Listener) receives a new message during a Pause Session it will
 * start a PauseMessageReceivedService.  This service will be responsible for sending Pause bounce back
 * messages and informing the rest of the app that a new message has been received
 * (through the event bus) so that any subscribed screens can update their UI elements.  After this
 * intent has been handled it will stop itself.
 */
public class PauseMessageReceivedService extends IntentService {

    @Inject
    protected Bus mBus;
    @Inject
    protected SharedPreferences mPrefs;

    private PauseSession mCurrentPauseSession;
    private PauseConversation mCurrentConversation;
    private String mContactId;

    public PauseMessageReceivedService() {
        super("PauseMessageReceivedService");
        Injector.inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        if(extras != null) {
            PauseMessage messageReceived = intent.getParcelableExtra(Constants.Message.MESSAGE_PARCEL);
            updatePauseSession(messageReceived);
        }
    }

    /**
     * Since PauseMessageReceivedService is only initiated from a PauseSessionService,
     * we can assume that our Pause Application will always have a non-null Session at this point.
     * This function will be used to update the active session by updating or creating a new
     * Conversation based on the message received.
     * The creating or modifying of conversations should happen here, not in Session!
     * @param message
     */
    private void updatePauseSession(PauseMessage message) {
        mCurrentPauseSession = PauseApplication.getCurrentSession();

        // Attempt to retrieve existing conversation
        mCurrentConversation = mCurrentPauseSession.getConversationBySender(message.getFrom());
        mContactId = lookupSender(message.getFrom());

        // If no current conversation exists with sender, create new one, then add message
        if (mCurrentConversation == null)
            mCurrentConversation = new PauseConversation(message.getFrom());
        mCurrentConversation.addMessageFromContact(message);

        // Check who created the Session to in order to send appropriate message
        Log.i("Message Recieved",mCurrentConversation.getMessagesReceived().size() + " messages in conversation");
        if(mCurrentPauseSession.shouldSenderReceivedBounceback(mContactId) && mCurrentConversation.getMessagesSentFromUser().size() == 0 ) {
            PauseBounceBackMessage bounceBackMessage = getBounceBackMessage();
            PauseApplication.messageSender.sendSmsMessage(message.getFrom(), bounceBackMessage);
            mCurrentConversation.addMessageFromPause(bounceBackMessage);
            mCurrentPauseSession.incrementResponseCount();
        }

        PauseApplication.updateNotifications();

        // Update Session conversations with updated conversation
        mCurrentPauseSession.updateConversation(mCurrentConversation);

        // alert UI that Pause Conversation has been updated
        mBus.post(new PauseMessageReceivedEvent());

    }

    private String lookupSender(String sender) {
        String contactId = "";
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(sender));

        ContentResolver contentResolver = getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID}, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();

                contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));

            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return contactId;

    }

    private PauseBounceBackMessage getBounceBackMessage() {
        return new PauseBounceBackMessage("Away",mCurrentConversation.getStringForBounceBackMessage());
    }

}
