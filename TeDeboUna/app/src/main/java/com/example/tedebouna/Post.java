package com.example.tedebouna;

public class Post {
    private String content;
    private String imageUrl;
    private String userId;
    private String userEmail; // Nuevo campo para el correo electrónico del usuario
    private long timestamp;

    // Constructor vacío requerido para Firestore
    public Post() {}

    public Post(String content, String imageUrl, String userId, String userEmail, long timestamp) {
        this.content = content;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.userEmail = userEmail; // Asignar el correo electrónico del usuario
        this.timestamp = timestamp;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
