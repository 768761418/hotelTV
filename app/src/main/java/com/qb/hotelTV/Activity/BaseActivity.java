package com.qb.hotelTV.Activity;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {

    public String getLocation(){
        String locationString = "";
        // 获取 LocationManager 实例
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 请求最近一次的位置信息
        try {
            // 获取最近一次的位置信息
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastKnownLocation==null){
                double latitude = 0;
                double longitude = 0;
                // 将经纬度保留两位小数并合成字符串
                locationString = String.format("%.2f,%.2f",  longitude, latitude);
                return locationString;
            }
            do{
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                // 将经纬度保留两位小数并合成字符串
                locationString = String.format("%.2f,%.2f",  longitude, latitude);
                // 在此处处理获取到的经纬度信息
            } while(lastKnownLocation == null);
            return locationString;
        } catch (SecurityException e) {
            return locationString;
        }
    }
}
