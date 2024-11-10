package com.qb.hotelTV.Data;

import android.util.Log;

import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.huibuTv.MyApplication;

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
    private String TAG = "CommonData";

    public void setData(String serverAddress,String tenant,String roomNumber){
        this.roomNumber = roomNumber;
        this.serverAddress = serverAddress;
        this.tenant = tenant;
        this.isLogin = true;
        Log.d(TAG, "setDataServerAddress: " + this.serverAddress);
        Log.d(TAG, "setDataTenant: " + this.tenant);
        Log.d(TAG, "setDataRoomNumber: " + this.roomNumber);
    }

    public String[] getData(){
        String[] data = new String[3];
        data[0] = this.serverAddress;
        data[1] = this.tenant;
        data[2] = this.roomNumber;

        Log.d(TAG, "getDataServerAddress: " + this.serverAddress);
        Log.d(TAG, "getDataTenant: " + this.tenant);
        Log.d(TAG, "getDataRoomNumber: " + this.roomNumber);
        return data;
    }

    public String getServerAddress(){
        return this.serverAddress;
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
