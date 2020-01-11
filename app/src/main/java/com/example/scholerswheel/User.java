package com.example.scholerswheel;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String phone;
    private String email;
    private String studentDetails;
    private boolean verified;
    private boolean admin;

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    private String uid;

    public User() {}

    public User(String name, String phone, String email, String studentDetails) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.studentDetails = studentDetails;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentDetails() {
        return studentDetails;
    }

    public void setStudentDetails(String studentDetails) {
        this.studentDetails = studentDetails;
    }
}
