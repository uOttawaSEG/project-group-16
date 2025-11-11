package com.example.proj16v2.ui.admin;

import com.example.proj16v2.Model.User;

public class Administrator extends User {
    private static final String ADMIN_EMAIL = "admin@uottawa.ca";
    private static final String ADMIN_PASSWORD = "12345";

    public Administrator() {
        super(ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    public static boolean isAdmin(String email, String password) {
        return ADMIN_EMAIL.equalsIgnoreCase(email)
                && ADMIN_PASSWORD.equals(password);
    }
}
