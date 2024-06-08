package com.qb.hotelTV.Model;

public class HotelListModel {

    private int id;
    private String name;
    private String picUrl;
    private int type;
    private String backgroundUrl;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public int getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getBackgroundUrl() {
        return backgroundUrl;
    }

    public void setBackgroundUrl(String backgroundUrl) {
        this.backgroundUrl = backgroundUrl;
    }
}
