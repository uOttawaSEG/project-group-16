package com.example.otams.model;

import java.unit.List;
public class Tutor extends User {
    private String fistName;
    private String lastName;
    private String phoneNumber;
    private String highestDegree;
    private List<String> coursesOffered;

    public Tutor(String firstName,String lastName, String phoneNumber, String highestDegree, List<String> coursesOffered, String email, String password) {
        super(email, password);
        this.fistName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.highestDegree = highestDegree;
        this.coursesOffered = coursesOffered;
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
    public String getHighestDegree() {
        return highestDegree;
    }
    public List<String> getCoursesOffered() {
        return coursesOffered;
    }
}
