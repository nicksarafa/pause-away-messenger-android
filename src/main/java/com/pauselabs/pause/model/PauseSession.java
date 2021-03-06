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
 * Each time a new Pause state is initiated we will generate a pause session. This session will keep
 * track of the conversations taking place, messages received, bounce backs sent, and any
 * information related to the Pause scoreboard, and duration of the Pause session.
 *
 * <p>IMPORTANT: All autoresponse settings and blacklist are retrieved at session creation and new
 * changes will not take effect until a new session is started!
 */
public class PauseSession implements Serializable {

  private static final long serialVersionUID = 1L;

  @Inject protected SharedPreferences mPrefs;

  private Long createdOn;
  private boolean isActive;
  private int creator;
  private int responseCount;

  private ArrayList<PauseConversation> conversations;

  private Set<String> mBlacklistContacts;
  private Set<String> mIcelistContacts;
  private Boolean smsPrivacySetting;
  private Boolean callPrivacySetting;

  public PauseSession(int sessionCreator) {
    Injector.inject(this);

    Date date = new Date();
    createdOn = date.getTime();
    creator = sessionCreator;
    conversations = new ArrayList<>();
    isActive = Boolean.TRUE;
    responseCount = 0;

    mBlacklistContacts = retrieveBlacklistContacts();
    mIcelistContacts = retrieveIcelistContacts();
    smsPrivacySetting =
        mPrefs.getBoolean(Constants.Settings.REPLY_SMS_KEY, Constants.Settings.DEFAULT_REPLY_SMS);
    callPrivacySetting =
        mPrefs.getBoolean(
            Constants.Settings.REPLY_MISSED_CALL_KEY, Constants.Settings.DEFAULT_REPLY_SMS);
  }

  public ArrayList<PauseConversation> getConversations() {
    return conversations;
  }

  public ArrayList<PauseConversation> getConversationsInTimeOrder() {
    Collections.sort(
        conversations,
        new Comparator<PauseConversation>() {
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

  public boolean isActive() {
    return isActive;
  }

  public void deactivateSession() {
    isActive = false;
  }

  public int getCreator() {
    return creator;
  }

  public void setCreator(int c) {
    creator = c;
  }

  /**
   * This function will attempt to retrieve a conversation given a sender. If no conversation is
   * found it will return a null conversation
   *
   * @param contact
   * @return
   */
  public PauseConversation getConversationByContactNumber(String contact) {
    PauseConversation requestedConversation = null;
    for (PauseConversation conversation : conversations) {
      if (conversation.getContactNumber().equals(contact)) {
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
   *
   * @param contactId
   * @return true if sender is safe to respond to
   */
  public Boolean shouldSenderReceivedBounceback(String contactId, int type) {
    Boolean shouldSendBounceback;

    if (!mPrefs.getBoolean(Constants.Pause.ONBOARDING_FINISHED_KEY, false)
        || mBlacklistContacts.contains(contactId)) {
      shouldSendBounceback = false;
    } else {
      shouldSendBounceback = privacyCheckPassed(contactId, type);
    }

    return shouldSendBounceback;
  }

  public Boolean isIced(String contactId) {
    return mIcelistContacts.contains(contactId);
  }

  private Set<String> retrieveBlacklistContacts() {
    return mPrefs.getStringSet(Constants.Settings.BLACKLIST, new HashSet<String>());
  }

  private Set<String> retrieveIcelistContacts() {
    return mPrefs.getStringSet(Constants.Settings.ICELIST, new HashSet<String>());
  }

  private Boolean privacyCheckPassed(String contactId, int type) {
    return ((smsPrivacySetting && type == Constants.Message.Type.SMS_INCOMING)
            || (callPrivacySetting && type == Constants.Message.Type.PHONE_INCOMING))
        && (!contactId.isEmpty()
            || mPrefs.getBoolean(
                Constants.Settings.REPLY_STRANGERS_KEY,
                Constants.Settings.DEFAULT_REPLY_STRANGERS));
  }
}
