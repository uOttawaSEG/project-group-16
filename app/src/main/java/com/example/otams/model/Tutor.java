package com.example.otams.model;

import java.util.List;
public class Tutor extends User {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String highestDegree;
    private List<String> coursesOffered;

    public Tutor(String firstName,String lastName, String phoneNumber, String highestDegree, List<String> coursesOffered, String email, String password) {
        super(email, password);
        this.firstName = firstName;
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
