package com.legaljudgements.admin.judgements.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ng on 2/19/2017.
 */

public class JudgementModel implements Parcelable {

    private String judgementId;

    private String title;
    private String court;
    private String judgementType;
    private String dateOfJudgement;
    private String caseTitle;
    private String caseType;
    private String shortDescription;
    private String detailedDescription;
    private String judgementDoc;
    private String googleDriveUrl;
    private Boolean isPublic;
    private Boolean isActive;
    private Boolean isFlagged;
    private String createdDate;


    public JudgementModel(Parcel in) {
        judgementId = in.readString();
        title = in.readString();
        court = in.readString();
        judgementType = in.readString();
        dateOfJudgement = in.readString();
        caseTitle = in.readString();
        caseType = in.readString();
        shortDescription = in.readString();
        detailedDescription = in.readString();
        judgementDoc = in.readString();
        googleDriveUrl = in.readString();
        byte tmpIsPublic = in.readByte();
        isPublic = tmpIsPublic == 0 ? null : tmpIsPublic == 1;
        byte tmpIsActive = in.readByte();
        isActive = tmpIsActive == 0 ? null : tmpIsActive == 1;
        byte tmpIsFlagged = in.readByte();
        isFlagged = tmpIsFlagged == 0 ? null : tmpIsFlagged == 1;
        createdDate = in.readString();
    }

    public static final Creator<JudgementModel> CREATOR = new Creator<JudgementModel>() {
        @Override
        public JudgementModel createFromParcel(Parcel in) {
            return new JudgementModel(in);
        }

        @Override
        public JudgementModel[] newArray(int size) {
            return new JudgementModel[size];
        }
    };

    public JudgementModel() {
        
    }

    public String getJudgementId() {
        return judgementId;
    }

    public void setJudgementId(String judgementId) {
        this.judgementId = judgementId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourt() {
        return court;
    }

    public void setCourt(String court) {
        this.court = court;
    }

    public String getJudgementType() {
        return judgementType;
    }

    public void setJudgementType(String judgementType) {
        this.judgementType = judgementType;
    }

    public String getDateOfJudgement() {
        return dateOfJudgement;
    }

    public void setDateOfJudgement(String dateOfJudgement) {
        this.dateOfJudgement = dateOfJudgement;
    }

    public String getCaseTitle() {
        return caseTitle;
    }

    public void setCaseTitle(String caseTitle) {
        this.caseTitle = caseTitle;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public String getJudgementDoc() {
        return judgementDoc;
    }

    public void setJudgementDoc(String judgementDoc) {
        this.judgementDoc = judgementDoc;
    }

    public String getGoogleDriveUrl() {
        return googleDriveUrl;
    }

    public void setGoogleDriveUrl(String googleDriveUrl) {
        this.googleDriveUrl = googleDriveUrl;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getFlagged() {
        if (isFlagged == null)
            return false;
        return isFlagged;
    }

    public void setFlagged(Boolean flagged) {
        isFlagged = flagged;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(judgementId);
        parcel.writeString(title);
        parcel.writeString(court);
        parcel.writeString(judgementType);
        parcel.writeString(dateOfJudgement);
        parcel.writeString(caseTitle);
        parcel.writeString(caseType);
        parcel.writeString(shortDescription);
        parcel.writeString(detailedDescription);
        parcel.writeString(judgementDoc);
        parcel.writeString(googleDriveUrl);
        parcel.writeByte((byte) (isPublic == null ? 0 : isPublic ? 1 : 2));
        parcel.writeByte((byte) (isActive == null ? 0 : isActive ? 1 : 2));
        parcel.writeByte((byte) (isFlagged == null ? 0 : isFlagged ? 1 : 2));
        parcel.writeString(createdDate);
    }
}
