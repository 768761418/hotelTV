package com.qb.hotelTV.Model;

public class ApkModel {
    private  int id;
    private  String name;
    private  String backGroundColor;
    private  String schemeUrl;


    public ApkModel(int id, String name, String backGroundColor, String schemeUrl) {
        this.id = id;
        this.name = name;
        this.backGroundColor = backGroundColor;
        this.schemeUrl = schemeUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBackGroundColor() {
        return backGroundColor;
    }

    public void setBackGroundColor(String backGroundColor) {
        this.backGroundColor = backGroundColor;
    }

    public String getSchemeUrl() {
        return schemeUrl;
    }

    public void setSchemeUrl(String schemeUrl) {
        this.schemeUrl = schemeUrl;
    }
}
