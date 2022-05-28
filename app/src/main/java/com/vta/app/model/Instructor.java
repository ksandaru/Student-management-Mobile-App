package com.vta.app.model;

public class Instructor {

    private String fullName;
    private String Email;
    private String NIC;
    private String cource;
    private String contactNo;

    public Instructor() {
    }

    public Instructor(String fullName, String email, String NIC, String cource, String contactNo) {
        this.fullName = fullName;
        Email = email;
        this.NIC = NIC;
        this.cource = cource;
        this.contactNo = contactNo;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getNIC() {
        return NIC;
    }

    public void setNIC(String NIC) {
        this.NIC = NIC;
    }

    public String getCource() {
        return cource;
    }

    public void setCource(String cource) {
        this.cource = cource;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
}
