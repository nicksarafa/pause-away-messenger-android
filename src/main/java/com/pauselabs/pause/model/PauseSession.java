package com.pauselabs.pause.model;

import android.content.SharedPreferences;

import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

/**
 * Each time a new Pause state is initiated we will generate a pause session.
 * This session will keep track of the conversations taking place, messages received, bounce backs sent, and any information related
 * to the Pause scoreboard, and duration of the Pause session.
 *
 * IMPORTANT: All autoresponse settings and blacklist are retrieved at session creation and new
 * changes will not take effect until a new session is started!
 */
public class PauseSession implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    protected SharedPreferences mPrefs;

    private Long createdOn;
    private boolean isActive;
    private int creator;
    private int responseCount;

    private ArrayList<PauseConversation> conversations;

    private Set<String> mBlacklistContacts;
    private Set<String> mWhitelistContacts;
    private String smsPrivacySetting;
    private String callPrivacySetting;

    public PauseSession(int sessionCreator) {
        Injector.inject(this);

        Date date = new Date();
        createdOn = date.getTime();
        creator = sessionCreator;
        conversations = new ArrayList<>();
        isActive = Boolean.TRUE;
        responseCount = 0;

        mBlacklistContacts = retrieveBlacklistContacts();
        mWhitelistContacts = retrieveWhitelistContacts();
        smsPrivacySetting = mPrefs.getString(Constants.Settings.REPLY_SMS, Constants.Privacy.EVERYBODY);
        callPrivacySetting = mPrefs.getString(Constants.Settings.REPLY_MISSED_CALL, Constants.Privacy.EVERYBODY);
    }

    public ArrayList<PauseConversation> getConversations() {
        return conversations;
    }

    public ArrayList<PauseConversation> getConversationsInTimeOrder() {
        Collections.sort(conversations, new Comparator<PauseConversation>() {
            @Override
            public int compare(PauseConversation msg1, PauseConversation msg2) {
                return msg1.getLastMessage().getDate().compareTo(msg2.getLastMessage().getDate());
            }
        });
        Collections.reverse(conversations);

        return conversations;
    }

    public void incrementResponseCount() {
        responseCount++;
    }

    public int getResponseCount() {
        return responseCount;
    }

    public boolean isActive(){ return isActive; }
    public void deactivateSession(){ isActive = false; }

    public int getCreator() { return creator; }
    public void setCreator(int c) { creator = c; }

    /**
     * This function will attempt to retrieve a conversation given a sender.  If no conversation is
     * found it will return a null conversation
     * @param contact
     * @return
     */
    public PauseConversation getConversationByContactNumber(String contact){
        PauseConversation requestedConversation = null;
        for(PauseConversation conversation: conversations){
            if(conversation.getContactNumber().equals(contact)){
                requestedConversation = conversation;
                break;
            }
        }

        if (requestedConversation == null) {
            requestedConversation = new PauseConversation(contact);
            PauseApplication.getCurrentSession().getConversations().add(requestedConversation);
        }

        return requestedConversation;
    }

    /**
     * This function will perform a check against the current user settings & blacklist to determine
     * whether a sender should receive a bounceback message
     * @param contactId
     * @return true if sender is safe to respond to
     */
    public Boolean shouldSenderReceivedBounceback(String contactId) {
        Boolean shouldSendBounceback;

        if(mBlacklistContacts.contains(contactId)) {
            shouldSendBounceback = false;
        } else {
            shouldSendBounceback = privacyCheckPassed(contactId);
        }

        return shouldSendBounceback;
    }

    public Boolean isWhiteListed(String contactId) {
        return mWhitelistContacts.contains(contactId);
    }

    private Set<String> retrieveBlacklistContacts() {
        return mPrefs.getStringSet(Constants.Settings.BLACKLIST, new HashSet<String>());
    }

    private Set<String> retrieveWhitelistContacts() {
        return mPrefs.getStringSet(Constants.Settings.WHITELIST, new HashSet<String>());
    }

    private Boolean privacyCheckPassed(String contactId) {
        if(smsPrivacySetting.equals(Constants.Privacy.CONTACTS_ONLY)) {
            return !contactId.isEmpty();
        } else if(smsPrivacySetting.equals(Constants.Privacy.EVERYBODY)) {
            return true;
        } else {
            return false;
        }
    }




}