package com.legaljudgements.admin.membership.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DetailInfo implements Parcelable {

    private String description = "";
    private String duration = "";
    private String price = "";
    private String id = "";

    public DetailInfo(Parcel in) {
        description = in.readString();
        duration = in.readString();
        price = in.readString();
        id = in.readString();
    }

    public static final Creator<DetailInfo> CREATOR = new Creator<DetailInfo>() {
        @Override
        public DetailInfo createFromParcel(Parcel in) {
            return new DetailInfo(in);
        }

        @Override
        public DetailInfo[] newArray(int size) {
            return new DetailInfo[size];
        }
    };

    public DetailInfo() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(duration);
        dest.writeString(price);
        dest.writeString(id);
    }
}