package com.pauselabs.pause.models;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.SavedPauseDataSource;
import java.io.Serializable;
import java.util.ArrayList;
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
  private Boolean isActive;
  private SavedPauseDataSource mDatasource;
  private int responseCount;

  private ArrayList<PauseConversation> conversations;

  private Set<String> mBlacklistContacts;
  private String smsPrivacySetting;
  private String callPrivacySetting;

  public PauseSession() {
    Injector.inject(this);

    Date date = new Date();
    createdOn = date.getTime();
    conversations = new ArrayList<PauseConversation>();
    mDatasource = new SavedPauseDataSource(PauseApplication.getInstance().getApplicationContext());
    isActive = Boolean.TRUE;
    responseCount = 0;

    mBlacklistContacts = retrieveBlacklistContacts();
    smsPrivacySetting =
        mPrefs.getString(Constants.Settings.REPLY_SMS, Constants.Privacy.CONTACTS_ONLY);
    callPrivacySetting =
        mPrefs.getString(Constants.Settings.REPLY_MISSED_CALL, Constants.Privacy.CONTACTS_ONLY);
  }

  public ArrayList<PauseConversation> getConversations() {
    return conversations;
  }

  public void incrementResponseCount() {
    responseCount++;
  }

  public int getResponseCount() {
    return responseCount;
  }

  public Boolean isActive() {
    return isActive;
  }

  public void deactivateSession() {
    isActive = Boolean.FALSE;
  }

  public Boolean conversationAlreadyExists(String sender) {
    Boolean result = false;
    for (PauseConversation conversation : conversations) {
      if (conversation.getSender().equals(sender)) {
        result = true;
        break;
      }
    }
    return result;
  }

  public void updateConversation(PauseConversation conversation) {
    Boolean updated = false;

    for (int i = 0; i < conversations.size(); i++) {
      PauseConversation currentConversation = conversations.get(i);
      if (currentConversation.getSender().equals(conversation.getSender())) {
        // update arraylist content
        conversations.set(i, conversation);
        updated = true;
      }
    }

    if (!updated) {
      conversations.add(conversation);
      updated = true;
    }
  }

  /**
   * This function will attempt to retrieve a conversation given a sender. If no conversation is
   * found it will return a null conversation
   *
   * @param sender
   * @return
   */
  public PauseConversation getConversationBySender(String sender) {
    PauseConversation requestedConversation = null;
    for (PauseConversation conversation : conversations) {
      if (conversation.getSender().equals(sender)) {
        requestedConversation = conversation;
        break;
      }
    }
    return requestedConversation;
  }

  public PauseBounceBackMessage getActiveBounceBackMessage() {
    PauseBounceBackMessage mActivePause;
    mDatasource.open();

    SharedPreferences mPrefs =
        PreferenceManager.getDefaultSharedPreferences(
            PauseApplication.getInstance().getApplicationContext());
    long id = mPrefs.getLong(Constants.Pause.ACTIVE_PAUSE_DATABASE_ID_PREFS, 0L);
    mActivePause = mDatasource.getSavedPauseById(id);

    mDatasource.close();

    return mActivePause;
  }

  /**
   * This function will perform a check against the current user settings & blacklist to determine
   * whether a sender should receive a bounceback message
   *
   * @param contactId
   * @return true if sender is safe to respond to
   */
  public Boolean shouldSenderReceivedBounceback(String contactId) {
    Boolean shouldSendBounceback = true;

    if (mBlacklistContacts.contains(contactId)) {
      shouldSendBounceback = false;
    } else {
      shouldSendBounceback = privacyCheckPassed(contactId);
    }

    return shouldSendBounceback;
  }

  private Set<String> retrieveBlacklistContacts() {
    return mPrefs.getStringSet(Constants.Settings.BLACKLIST, new HashSet<String>());
  }

  private Boolean privacyCheckPassed(String contactId) {
    if (smsPrivacySetting.equals(Constants.Privacy.CONTACTS_ONLY)) {
      return !contactId.isEmpty();
    } else if (smsPrivacySetting.equals(Constants.Privacy.EVERYBODY)) {
      return true;
    } else {
      return false;
    }
  }
}
