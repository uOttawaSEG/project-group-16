package com.example.otams.model;

public class Student extends User {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String programOfStudy;

    public Student(String firstName, String lastName, String phoneNumber, String programOfStudy, String email, String password) {
        super(email, password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.programOfStudy = programOfStudy;
    }

    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getProgramOfStudy() {
        return programOfStudy;
    }
}
