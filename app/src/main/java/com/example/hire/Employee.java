package com.example.hire;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.database.Exclude;

public class Employee {

    private String mName;
    private String mPosition;
    private String mImageUrl;
    private String resumeImageUrl;
    private String key;
    private String mAddress;
    private String mPhoneNumber;
    private String mEmail;
    private String recruitedDate;
    private String mSkills;
    private String mEducation;
    private int mAge;

    public Employee() {
    }

    public int getmAge() {
        return mAge;
    }

    public void setmAge(int mAge) {
        this.mAge = mAge;
    }

    public String getmSkills() {
        return mSkills;
    }

    public void setmSkills(String mSkills) {
        this.mSkills = mSkills;
    }

    public String getmEducation() {
        return mEducation;
    }

    public void setmEducation(String mEducation) {
        this.mEducation = mEducation;
    }

    public Employee(String mName, String mImageUrl, String resumeImageUrl, String mAddress, String mPhoneNumber, String mEmail, String recruitedDate, String mSkills, String mEducation, int mAge) {
        this.mName = mName;
        this.mImageUrl = mImageUrl;
        this.resumeImageUrl = resumeImageUrl;
        this.mAddress = mAddress;
        this.mPhoneNumber = mPhoneNumber;
        this.mEmail = mEmail;
        this.recruitedDate = recruitedDate;
        this.mSkills = mSkills;
        this.mEducation = mEducation;
        this.mPosition = "Employee";
        this.mAge = mAge;
    }

    public String getRecruitedDate() {
        return recruitedDate;
    }

    public void setRecruitedDate(String recruitedDate) {
        this.recruitedDate = recruitedDate;
    }

    public String getResumeImageUrl() {
        return resumeImageUrl;
    }

    public void setResumeImageUrl(String resumeImageUrl) {
        this.resumeImageUrl = resumeImageUrl;
    }


    public String getmAddress() {
        return mAddress;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public String getmPhoneNumber() {
        return mPhoneNumber;
    }

    public void setmPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }


    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmPosition() {
        return mPosition;
    }

    public void setmPosition(String mPosition) {
        this.mPosition = mPosition;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    //exclude from firebase database
    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }
}