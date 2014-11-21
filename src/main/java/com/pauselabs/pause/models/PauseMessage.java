package com.pauselabs.pause.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.SmsMessage;
import com.pauselabs.pause.core.Constants;

import java.io.Serializable;
import java.util.Date;

/**
 * Messages represent incoming messages from SMS, MMS, and third party messengers
 */
public class PauseMessage implements Serializable, Parcelable {

    public static final Parcelable.Creator<PauseMessage> CREATOR
            = new Parcelable.Creator<PauseMessage>() {
        public PauseMessage createFromParcel(Parcel in) {
            return new PauseMessage(in);
        }

        public PauseMessage[] newArray(int size) {
            return new PauseMessage[size];
        }
    };

    private static final long serialVersionUID = 1L;
    private String sender;
    private String text;
    private Long receivedOn;
    private String type;


    public PauseMessage(String sender, String text) {
        this.sender = sender;
        this.text = text;
        receivedOn = new Date().getTime();
    }

    public PauseMessage(SmsMessage smsMessage){
        this.sender = smsMessage.getDisplayOriginatingAddress();
        this.text = smsMessage.getDisplayMessageBody();
        this.receivedOn = smsMessage.getTimestampMillis();
        this.type = Constants.Message.SMS_TYPE;
    }

    public PauseMessage(Parcel in){
        this.sender = in.readString();
        this.text = in.readString();
        this.receivedOn = in.readLong();
        this.type = in.readString();

    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setReceivedOn(Long time) {
        this.receivedOn = time;
    }

    public Long getReceivedOn() {
        return receivedOn;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(sender);
        out.writeString(text);
        out.writeLong(receivedOn);
        out.writeString(type);

    }
}
