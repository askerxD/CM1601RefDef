package com.example.cm1601refdef.objects;

public class Dealer {

    private String dealerId;
    private String name;
    private String contactNumber;
    private String location;

    public Dealer() {
    }
    public Dealer(String dealerId, String name, String contactNumber, String location) {
        this.dealerId = dealerId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.location = location;
    }

    public String getDealerId() {
        return dealerId;
    }
    public void setDealerId(String dealerId) {
        this.dealerId = dealerId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getContactNumber() {
        return contactNumber;
    }
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return dealerId + "," + name + "," + contactNumber + "," + location;
    }
}