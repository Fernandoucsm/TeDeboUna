package com.example.tedebouna;

public class Comment {
    private String userId;
    private String content;
    private String userName;
    private String userEmail;
    private String userProfileImageUrl;

    // Firestore requires a no-arg constructor
    public Comment() {}

    public Comment(String userId, String content, String userName, String userEmail, String userProfileImageUrl) {
        this.userId = userId;
        this.content = content;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userProfileImageUrl = userProfileImageUrl;
    }

    // existing getters and setters...
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getContent() {
        return content;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }
}