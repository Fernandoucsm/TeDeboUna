package com.example.tedebouna;

public class Post {
    private String userProfileImageUrl; // New field for the user's profile image URL

    private String content;
    private String imageUrl;
    private String userId;
    private String userEmail;
    private String userName; // Nuevo campo para el nombre del usuario
    private long timestamp;

    // Constructor vacío requerido para Firestore
    public Post() {}

    public Post(String content, String imageUrl, String userId, String userEmail, String userName, long timestamp) {
        this.content = content;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userName = userName; // Asignar el nombre del usuario
        this.timestamp = timestamp;
    }
    public String getUserProfileImageUrl() {
        return userProfileImageUrl;
    }

    public void setUserProfileImageUrl(String userProfileImageUrl) {
        this.userProfileImageUrl = userProfileImageUrl;
    }
    // Getters y setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}