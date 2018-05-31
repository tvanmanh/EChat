package com.project.tranvanmanh.e_chat;

import org.parceler.Parcel;

/**
 * Created by tranvanmanh on 4/29/2018.
 */
@Parcel
public class UserProfile {

    String thumb_image;
    String name;
    String status;
    String achivement;
    String career;
    String school;
    String home_address;
    String image;
    String device_token;

    public UserProfile() {
    }

    public UserProfile(String thumb_image, String name,String status, String achivement, String career, String school, String home_address, String image) {
        this.thumb_image = thumb_image;
        this.name = name;
        this.status = status;
        this.achivement = achivement;
        this.career = career;
        this.school = school;
        this.home_address = home_address;
        this.image = image;
    }

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumb_image() {
        return thumb_image;
    }

    public void setThumb_image(String thumb_image) {
        this.thumb_image = thumb_image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAchivement() {
        return achivement;
    }

    public void setAchivement(String achivement) {
        this.achivement = achivement;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getHome_address() {
        return home_address;
    }

    public void setHome_address(String home_address) {
        this.home_address = home_address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
