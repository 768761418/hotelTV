package com.qb.hotelTV.Model;

public class ApkModel {
    private  int id;
    private  String name;
    private  String logoUrl;
    private  String backgroundColor;
    private  String schemeUrl;

    public ApkModel(int id, String name, String logoUrl, String backgroundColor, String schemeUrl) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.backgroundColor = backgroundColor;
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

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getSchemeUrl() {
        return schemeUrl;
    }

    public void setSchemeUrl(String schemeUrl) {
        this.schemeUrl = schemeUrl;
    }
}
