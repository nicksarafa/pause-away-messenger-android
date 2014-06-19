package com.pauselabs.pause.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Each time a new Pause state is initiated we will generate a pause session.
 * This session will keep track of the conversations taking place, messages received, bounce backs sent, and any information related
 * to the Pause scoreboard, and duration of the Pause session
 */
public class PauseSession implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long createdOn;
    private Boolean isActive;

    private ArrayList<PauseConversation> conversations;

    public PauseSession() {
        Date date = new Date();
        createdOn = date.getTime();
        conversations = new ArrayList<PauseConversation>();

        isActive = Boolean.TRUE;
    }



    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }


    public ArrayList<PauseConversation> getConversations() {
        return conversations;
    }

    public void setConversations(ArrayList<PauseConversation> conversations) {
        this.conversations = conversations;
    }

    public void addNewConversation(PauseConversation conversation) {
        conversations.add(conversation);
    }

    public void addNewConversation(PauseMessage message) {
        PauseConversation conversation = new PauseConversation(message.getSender());

    }

    public Boolean isActive(){
        return isActive;
    }

    public void deactivateSession(){
        isActive = Boolean.FALSE;
    }

    public Boolean conversationAlreadyExists(String sender){
        Boolean result = false;
        for(PauseConversation conversation: conversations){
            if(conversation.getSender().equals(sender)){
                result = true;
                break;
            }
        }
        return result;
    }


    public void updateConversation(PauseConversation conversation){
        Boolean updated = false;

        for(int i = 0; i < conversations.size(); i++) {
            PauseConversation currentConversation = conversations.get(i);
            if(currentConversation.getSender().equals(conversation.getSender())){
                // update arraylist content
                conversations.set(i, conversation);
                updated = true;
            }
        }

        if(!updated) {
            conversations.add(conversation);
            updated = true;
        }
    }

    /**
     * This function will attempt to retrieve a conversation given a sender.  If no conversation is
     * found it will return a null conversation
     * @param sender
     * @return
     */
    public PauseConversation getConversationBySender(String sender){
        PauseConversation requestedConversation = null;
        for(PauseConversation conversation: conversations){
            if(conversation.getSender().equals(sender)){
                requestedConversation = conversation;
                break;
            }
        }
        return requestedConversation;
    }


}