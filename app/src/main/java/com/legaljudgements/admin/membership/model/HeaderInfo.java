package com.legaljudgements.admin.membership.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HeaderInfo implements Parcelable {

    private String title;
    private String description = "";
    private String duration = "";
    private String price = "";
    private String id = "";
    private String scope = "";
    private String unit = "";

    public HeaderInfo(Parcel in) {
        title = in.readString();
        description = in.readString();
        duration = in.readString();
        price = in.readString();
        id = in.readString();
        scope = in.readString();
        unit = in.readString();
    }

    public static final Creator<HeaderInfo> CREATOR = new Creator<HeaderInfo>() {
        @Override
        public HeaderInfo createFromParcel(Parcel in) {
            return new HeaderInfo(in);
        }

        @Override
        public HeaderInfo[] newArray(int size) {
            return new HeaderInfo[size];
        }
    };

    public HeaderInfo() {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(duration);
        dest.writeString(price);
        dest.writeString(id);
        dest.writeString(scope);
        dest.writeString(unit);
    }
}