package com.vta.app.model;

import androidx.annotation.NonNull;

public class Student {
    private String fullName;
    private String NIC;
    private String Email;
    private String studentID;
    private String cource;
    private String dob;
    private String address;
    private String contactNo;

    public Student() {
    }

    public Student(String fullName, String NIC, String email, String studentID, String cource, String dob, String address, String contactNo) {
        this.fullName = fullName;
        this.NIC = NIC;
        Email = email;
        this.studentID = studentID;
        this.cource = cource;
        this.dob = dob;
        this.address = address;
        this.contactNo = contactNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNIC() {
        return NIC;
    }

    public void setNIC(String NIC) {
        this.NIC = NIC;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getCource() {
        return cource;
    }

    public void setCource(String cource) {
        this.cource = cource;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

}
