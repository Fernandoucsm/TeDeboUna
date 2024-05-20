package com.example.tedebouna;

import android.app.Application;
import android.graphics.Bitmap;

public class MyApp extends Application {
    private Bitmap profileImage;

    public Bitmap getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(Bitmap profileImage) {
        this.profileImage = profileImage;
    }
}