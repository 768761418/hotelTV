package com.qb.hotelTV.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesUtils {
    private static SharedPreferencesUtils instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private String KEY_SERVER_ADDRESS = "server_address";
    private String KEY_ROOM_NUMBER = "room_number";
    private String KEY_TENANT = "tenant";
    private String KEY_IS_FIRST_RUN = "isFirstRun";
    private String PREFS_NAME = "Common";


    private SharedPreferencesUtils(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharedPreferencesUtils getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesUtils(context.getApplicationContext());
        }
        return instance;
    }

    public void saveIsFirstRun(boolean isFirstRun){
        editor.putBoolean(KEY_IS_FIRST_RUN,isFirstRun);
    }
    public void saveServerAddress(String serverAddress){
        editor.putString(KEY_SERVER_ADDRESS,serverAddress);
        editor.apply();
    }
    public void saveRoomNumber(String roomNumber){
        editor.putString(KEY_ROOM_NUMBER,roomNumber);
        editor.apply();
    }
    public void saveTenant(String tenant){
        editor.putString(KEY_TENANT,tenant);
        editor.apply();
    }

    public void saveInitData(String serverAddress,String roomNumber,String tenant){
        editor.putBoolean(KEY_IS_FIRST_RUN,false);
        editor.putString(KEY_SERVER_ADDRESS,serverAddress);
        editor.putString(KEY_ROOM_NUMBER,roomNumber);
        editor.putString(KEY_TENANT,tenant);
        editor.apply();
    }

    //    读数据
    public String loadServerAddress() {
        return sharedPreferences.getString(KEY_SERVER_ADDRESS,"");
    }
    public String loadRoomNumber() {
        return sharedPreferences.getString(KEY_ROOM_NUMBER,"");
    }
    public String loadTenant() {
        return sharedPreferences.getString(KEY_TENANT,"");
    }


    public boolean loadIsFirstRun() {
        return sharedPreferences.getBoolean(KEY_IS_FIRST_RUN,true);
    }

    public String[] loadInitData(){
        String[] data = new String[3];
        data[0] = sharedPreferences.getString(KEY_SERVER_ADDRESS,"");
        data[1] = sharedPreferences.getString(KEY_ROOM_NUMBER,"");
        data[2] = sharedPreferences.getString(KEY_TENANT,"");
        return data;
    }


    public void clearData(){
        editor.remove(KEY_IS_FIRST_RUN);
        editor.remove(KEY_SERVER_ADDRESS);
        editor.remove(KEY_ROOM_NUMBER);
        editor.remove(KEY_TENANT);
        editor.apply();
    }




}
