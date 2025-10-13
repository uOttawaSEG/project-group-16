package com.example.otams.model;

public class User {
    private String email;
    private String password;

    public User(String emial, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
}
