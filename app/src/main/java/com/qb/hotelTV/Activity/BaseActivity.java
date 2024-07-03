package com.qb.hotelTV.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dreamgyf.android.ui.widget.textview.marquee.MarqueeTextView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.HttpUtil;
import com.qb.hotelTV.Activity.Hospital.HospitalActivity;
import com.qb.hotelTV.Activity.Hospital.HospitalListActivity;
import com.qb.hotelTV.Activity.Hospital.HospitalStartVideoActivity;
import com.qb.hotelTV.Activity.Hospital.HospitalVideoActivity;
import com.qb.hotelTV.Activity.Hospital.HospitalWebActivity;
import com.qb.hotelTV.Data.CommonData;
import com.qb.hotelTV.Handler.CrashHandler;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Http.LocationHttp;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Listener.WebSocketClient;
import com.qb.hotelTV.Model.HotelListModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Setting.ApplicationSetting;
import com.qb.hotelTV.Utils.PermissionUtils;
import com.qb.hotelTV.Utils.PlayerUtils;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.huibuTv.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class BaseActivity extends Activity {
    public FocusScaleListener focusScaleListener = new FocusScaleListener();
    private JSONObject hotelMessage = null;
    public CommonData commonData = CommonData.getInstance();
    private String TAG = "BaseActivity";
    private boolean isGetToken = true;







    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 闪退日志
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        //请求权限
        PermissionUtils permissionUtils = new PermissionUtils();
        permissionUtils.checkPermission(this);
    }

    //    获取经纬度
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
    //    请求天气和地址
    public  void getGeoAndWeather(TextView geoView,TextView weatherView){
        String locationString = getLocation();
//        请求地址
        LocationHttp.getInstance().getGeo(locationString, new LocationHttp.LocationHttpCallback() {
            @Override
            public void onResponse(String responseData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (geoView != null){
                            geoView.setText(responseData);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String failName) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (geoView != null){
                            geoView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
//        请求天气
        LocationHttp.getInstance().getWeather(locationString, new LocationHttp.LocationHttpCallback() {
            @Override
            public void onResponse(String responseData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weatherView != null){
                            weatherView.setText(responseData);
                        }
                    }
                });

            }

            @Override
            public void onFailure(String failName) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weatherView != null){
                            weatherView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }


//    获取酒店配置
    public JSONObject getHotelMessageFromHttp (String serverAddress, String tenant){
        hotelMessage = null;
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                hotelMessage = BackstageHttp.getInstance().getHotelMessage(serverAddress, tenant);
                latch.countDown();
            }
        }).start();
//
        try {
            latch.await(); // 等待请求完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return hotelMessage;
    }


//    初始化logo和background
    public void initLogoAndBackGround(Context context,ImageView logoView,String logoUrl,ImageView bgView,String bgUrl){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (logoView == null || logoUrl.equals("")){
                    if (logoView != null){
                        logoView.setVisibility(View.GONE);
                    }
                }else {
                    Glide.with((Activity) context)
                            .load(logoUrl)
                            .error(R.drawable.img)
                            .into(logoView);
                }

                if (bgView == null || bgUrl.equals("")){
                    if (bgView != null){
                        bgView.setVisibility(View.GONE);
                    }
                }else {
                    Glide.with((Activity) context)
                            .load(bgUrl)
                            .error(R.drawable.app_bg)
                            .into(bgView);
                }
            }
        });
    }



}
