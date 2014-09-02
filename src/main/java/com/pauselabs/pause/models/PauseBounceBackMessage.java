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
    private String pathToOriginal;
    private String pathToThumb;
    private String location;
    private Long endTime;
    private boolean isFavorite = false;
    private Long id = -1L;  // default unset value


    public PauseBounceBackMessage() {
        this.createdOn = new Date().getTime();
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
        pathToOriginal = in.readString();
        location = in.readString();
        endTime = in.readLong();
        isFavorite = in.readByte() != 0;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setPathToOriginal(String path){
        this.pathToOriginal = path;
    }

    public String getPathToOriginal() {
        return pathToOriginal;
    }

    public void setEndTime(Long endTime){
        this.endTime = endTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
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
        out.writeString(pathToOriginal);
        out.writeString(location);
        out.writeLong(endTime);
        out.writeByte((byte) (isFavorite ? 1 : 0));
    }

}
