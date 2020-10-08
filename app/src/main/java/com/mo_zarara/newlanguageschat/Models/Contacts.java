package com.mo_zarara.newlanguageschat.Models;

public class Contacts {

    private String name, userStatus, nativeLanguages, targetLanguages, image;

    public Contacts() {
    }

    public Contacts(String name, String userStatus, String nativeLanguages, String targetLanguages, String image) {
        this.name = name;
        this.userStatus = userStatus;
        this.nativeLanguages = nativeLanguages;
        this.targetLanguages = targetLanguages;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getUserStatus() {
        return userStatus;
    }

    public String getNativeLanguages() {
        return nativeLanguages;
    }

    public String getTargetLanguages() {
        return targetLanguages;
    }

    public String getImage() {
        return image;
    }
}
