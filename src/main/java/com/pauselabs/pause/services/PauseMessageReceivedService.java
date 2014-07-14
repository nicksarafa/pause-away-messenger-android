package com.pauselabs.pause.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
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

    private PauseBounceBackMessage retrieveActivePause() {
        PauseBounceBackMessage activePauseMessage = PauseApplication.getCurrentSession().getActiveBounceBackMessage();
        return activePauseMessage;
    }

    private PauseBounceBackMessage retrieveSecondaryPause() {
        PauseBounceBackMessage secondaryPause = new PauseBounceBackMessage();
        secondaryPause.setMessage(Constants.Pause.SECONDARY_BOUNCE_BACK_MESSAGE_TEXT);
        return secondaryPause;
    }

    /**
     * Since PauseMessageReceivedService is only initiated from a PauseSessionService,
     * we can assume that our Pause Application will always have a non-null Session at this point.
     * This function will be used to update the active session by updating or creating a new
     * Conversation based on the message received.
     * @param message
     */
    private void updatePauseSession(PauseMessage message) {

        // This class will handle all manipulation of conversations in conversation.
        // The creating or modifying of conversations should happen here, not in Session!

        PauseSession currentPauseSession = PauseApplication.getCurrentSession();
        // Attempt to retrieve existing conversation
        PauseConversation currentConversation = currentPauseSession.getConversationBySender(message.getSender());
        if(currentConversation != null) {
            // Update conversation
            currentConversation.addMessage(message);

            // Check if we should send second bounce back AFTER we add the message
            if(currentConversation.getMessagesReceived().size() == Constants.Pause.SECOND_BOUNCE_BACK_TRIGGER){
                // Send second Pause Bounce Back
                PauseApplication.messageSender.sendSmsMessage(message.getSender(), retrieveSecondaryPause());

                currentConversation.setSentSecondPause(true);
            }

        }
        else{
            // Create new conversation
            currentConversation = new PauseConversation(message.getSender());
            currentConversation.addMessage(message);

            // Send Pause Bounce Back
            PauseApplication.messageSender.sendMmsMessage(message.getSender(), retrieveActivePause());

            // Update conversation
            currentConversation.setSentPause(true);
        }

        // Update Session conversations with updated conversation
        currentPauseSession.updateConversation(currentConversation);

        // Save updated Pause Session data to DB

        // alert UI that Pause Conversation has been updated
        mBus.post(new PauseMessageReceivedEvent());

    }
}
