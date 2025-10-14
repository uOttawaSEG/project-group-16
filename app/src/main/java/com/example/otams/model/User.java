package com.example.otams.model;
import java.util.ArrayList;
import java.util.List;

public abstract class User {
    private String email;
    private String password;
    private String userLabel;

    //To save in memory
    public static final List<User> Users = new ArrayList<>();

    public User(String email, String password, String userLabel) {
        this.email = email.trim().toLowerCase();
        this.password = password;
        this.userLabel = userLabel;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public String welcomeMessage() {
        return "Welcome! You are logged in as " + userLabel;
    }
}
