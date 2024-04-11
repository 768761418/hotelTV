package com.qb.hotelTV.Model;

public class RoomMessageModel {

    private int id;
    private String roomNumber;
    private String roomName;
    private String wifiPassword;
    private String frontDeskPhone;
    private long createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getWifiPassword() {
        return wifiPassword;
    }

    public void setWifiPassword(String wifiPassword) {
        this.wifiPassword = wifiPassword;
    }

    public String getFrontDeskPhone() {
        return frontDeskPhone;
    }

    public void setFrontDeskPhone(String frontDeskPhone) {
        this.frontDeskPhone = frontDeskPhone;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }
}
