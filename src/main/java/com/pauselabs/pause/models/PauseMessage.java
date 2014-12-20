package com.pauselabs.pause.models;

import android.telephony.SmsMessage;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.listeners.PauseSmsListener;

import java.io.Serializable;
import java.util.Date;

/**
 * Messages represent incoming messages from SMS, MMS, and third party messengers
 */
public class PauseMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    // if to or from are equal to '0' it means the user
    private int id;
    private String from;
    private String to;
    private String message;
    private Long date;
    private int type;

    public PauseMessage(String from, String to, String messasge, Long dateInMillis, int type) {
        id = PauseSmsListener.getNewSmsCursor().getCount();
        if (type == Constants.Message.Type.SMS_PAUSE_OUTGOING)
            id++;

        this.from = from;
        this.to = to;
        this.message = messasge;
        this.date = dateInMillis;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setDate(Long date) {
        this.date = date;
    }

    public Long getDate() {
        return date;
    }

    public void setType(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

}
