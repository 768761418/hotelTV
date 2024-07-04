package com.qb.hotelTV.Data;

public class CommonData {
    // 单例实例，延迟初始化
    private static CommonData instance;


    // 私有构造函数防止外部实例化
    private CommonData() {}

    // 获取单例实例的静态方法
    public static synchronized CommonData getInstance() {
        if (instance == null) {
            instance = new CommonData();
        }
        return instance;
    }
    private String serverAddress;
    private String tenant;
    private String roomNumber;
    private boolean isLogin = false;

    public void setData(String serverAddress,String tenant,String roomNumber){
        this.roomNumber = roomNumber;
        this.serverAddress = serverAddress;
        this.tenant = tenant;
        this.isLogin = true;
    }

    public String[] getData(){
        String[] data = new String[3];
        data[0] = serverAddress;
        data[1] = tenant;
        data[2] = roomNumber;
        return data;
    }

    public void clearData(){
        tenant = null;
        serverAddress = null;
        roomNumber = null;
        isLogin = false;
    }

    public boolean getIsLogin(){
        return isLogin;
    }


}
