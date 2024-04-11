package com.qb.hotelTV.Model;

public class HotelMessageModel {
    private int id;
    private String name;
    private String iconUrl;
    private String homepageBackground;
    private long createTime;

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

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getHomepageBackground() {
        return homepageBackground;
    }

    public void setHomepageBackground(String homepageBackground) {
        this.homepageBackground = homepageBackground;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
