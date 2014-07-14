package com.pauselabs.pause.models;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Date;

/**
 * This is the Pause Bounce Back Message Object model
 */
public class PauseBounceBackMessage implements Parcelable, Serializable {

    public static final Parcelable.Creator<PauseBounceBackMessage> CREATOR
            = new Parcelable.Creator<PauseBounceBackMessage>() {
        public PauseBounceBackMessage createFromParcel(Parcel in) {
            return new PauseBounceBackMessage(in);
        }

        public PauseBounceBackMessage[] newArray(int size) {
            return new PauseBounceBackMessage[size];
        }
    };
    private static final long serialVersionUID = 1L;
    private String title;
    private String message;
    private Long createdOn;
    private Bitmap image;
    private Bitmap blurredImage;
    private String pathToImage;
    private String pathToThumb;
    private Long id;


    public PauseBounceBackMessage() {

    }

    public PauseBounceBackMessage(String title, String message) {
        this.title = title;
        this.message = message;
        this.createdOn = new Date().getTime();
    }

    public PauseBounceBackMessage(Parcel in){
        title = in.readString();
        message = in.readString();
        createdOn = in.readLong();
        id = in.readLong();
        pathToImage = in.readString();
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

    public Long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Long createdOn) {
        this.createdOn = createdOn;
    }

    public void setImage(Bitmap bitmap) {
        this.image = bitmap;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setId(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getPathToImage(){
        return pathToImage;
    }

    public void setPathToImage(String path) {
        this.pathToImage = path;
    }

    public String getPathToThumb() {
        return pathToThumb;
    }

    public void setPathToThumb(String path){
        this.pathToThumb = path;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int i) {
        out.writeString(title);
        out.writeString(message);
        out.writeLong(createdOn);
        out.writeLong(id);
        out.writeString(pathToImage);
    }

}
