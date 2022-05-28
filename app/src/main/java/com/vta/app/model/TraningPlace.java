package com.vta.app.model;

public class TraningPlace {

    private String org_name;
    private String org_address;
    private String  time_duration;
    private String start_date;
    private String end_date;

    public TraningPlace() {
    }

    public TraningPlace(String org_name, String org_address, String time_duration, String start_date, String end_date) {
        this.org_name = org_name;
        this.org_address = org_address;
        this.time_duration = time_duration;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getOrg_address() {
        return org_address;
    }

    public void setOrg_address(String org_address) {
        this.org_address = org_address;
    }

    public String getTime_duration() {
        return time_duration;
    }

    public void setTime_duration(String time_duration) {
        this.time_duration = time_duration;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }
}
