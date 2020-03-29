package com.example.hire;

public class Employee {

    private String mName,mPosition,mImageUrl;

    public Employee(String mName, String mImageUrl) {
        this.mName = mName;
        this.mImageUrl = mImageUrl;
    }

    public Employee(String mName, String mPosition, String mImageUrl) {
        this.mName = mName;
        this.mPosition = mPosition;
        this.mImageUrl = mImageUrl;
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

    public Employee(){

    }
}
