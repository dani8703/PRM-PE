package com.example.sp25_trandangquocdat_njs1706.model;

import com.google.gson.Gson;

public class AddressModel {
    private String address;
    private double lat;
    private double log;

    public AddressModel() {
    }

    public AddressModel(String address, double lat, double log) {
        this.address = address;
        this.lat = lat;
        this.log = log;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLog() {
        return log;
    }

    public void setLog(double log) {
        this.log = log;
    }

    public AddressModel getAddressFromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, AddressModel.class);
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String toJson(AddressModel addressModel){
        Gson gson = new Gson();
        return gson.toJson(addressModel);
    }
}
