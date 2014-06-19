package com.pauselabs.pause.models;



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
    private Boolean sentPause;
    private Boolean sentSecondPause;

    public PauseConversation(String sender) {
        this.sender = sender;
        messagesReceived = new ArrayList<PauseMessage>();

        Date date = new Date();
        initiatedOn = date.getTime(); // TODO This should be taken from message received
    }

    public void addMessage(PauseMessage message) {
        messagesReceived.add(message);
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



}
