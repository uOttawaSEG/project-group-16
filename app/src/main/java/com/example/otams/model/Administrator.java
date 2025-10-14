package com.example.otams.model;
public final class Administrator {
    // >>> Set your fixed admin username/password here <<<
    public static final String USERNAME = "admin@uottawa.ca"; //username
    public static final String PASSWORD = "admin123"; //password

    private Administrator() {
    }

    // Quick check that admin login is valid
    public static boolean isAdminValid(String email, String password) {
        if (email == null || password == null) return false;
        return USERNAME.equalsIgnoreCase(email.trim()) && PASSWORD.equals(password);
    }

    /** For display on the welcome screen */
    public static String roleLabel() {
        return "Administrator";
    }
}

