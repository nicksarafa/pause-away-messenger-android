package com.pauselabs.pause.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * This is the Pause Bounce Back Message Object model
 */
public class PauseBounceBackMessage implements Parcelable {

    public static final Parcelable.Creator<PauseBounceBackMessage> CREATOR
            = new Parcelable.Creator<PauseBounceBackMessage>() {
        public PauseBounceBackMessage createFromParcel(Parcel in) {
            return new PauseBounceBackMessage(in);
        }

        public PauseBounceBackMessage[] newArray(int size) {
            return new PauseBounceBackMessage[size];
        }
    };

    private String title;
    private String message;
    private Date createdOn;


    public PauseBounceBackMessage() {

    }

    public PauseBounceBackMessage(String title, String message) {
        this.title = title;
        this.message = message;
        this.createdOn = new Date();
    }

    public PauseBounceBackMessage(Parcel in){
        title = in.readString();
        message = in.readString();
        createdOn = new Date(in.readLong());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(title);
        out.writeString(message);
        out.writeLong(createdOn.getTime());
    }

}
