package com.example.hire.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "employee_table")
public class EmployeeEntity {

    @PrimaryKey(autoGenerate = true)
    private int key;
    private String name;
    private String position;
    private String profileImageUrl;
    private String resumeImageUrl;
    private String address;
    private String phoneNum;
    private String email;
    private String recruitedDate;
    private String skills;
    private String education;
    private int age;

    public EmployeeEntity(String name, String position, String profileImageUrl, String resumeImageUrl, String address, String phoneNum, String email, String recruitedDate, String skills, String education, int age) {
        this.name = name;
        this.position = position;
        this.profileImageUrl = profileImageUrl;
        this.resumeImageUrl = resumeImageUrl;
        this.address = address;
        this.phoneNum = phoneNum;
        this.email = email;
        this.recruitedDate = recruitedDate;
        this.skills = skills;
        this.education = education;
        this.age = age;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getResumeImageUrl() {
        return resumeImageUrl;
    }

    public void setResumeImageUrl(String resumeImageUrl) {
        this.resumeImageUrl = resumeImageUrl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRecruitedDate() {
        return recruitedDate;
    }

    public void setRecruitedDate(String recruitedDate) {
        this.recruitedDate = recruitedDate;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
