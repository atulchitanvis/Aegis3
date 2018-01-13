package com.example.android.spitit;

/**
 * Created by Aishwarya on 07-01-2018.
 */

public class EmergencyMessage {private String text;
    private String name;
    private String photoUrl;

    public EmergencyMessage() {
    }

    public EmergencyMessage(String text, String name, String photoUrl) {
        this.text = text;
        this.name = name;
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}

