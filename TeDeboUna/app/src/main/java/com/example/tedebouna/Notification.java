package com.example.tedebouna;

public class Notification {
    private String title;
    private String body;

    public Notification(String title, String body) {
        this.title = title;
        this.body = body;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) { // Add a space between String and title
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
