package com.qb.hotelTV.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.upstream.HttpUtil;
import com.qb.hotelTV.Activity.Hospital.HospitalActivity;
import com.qb.hotelTV.Data.CommonData;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Listener.WebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.CountDownLatch;

public class BaseActivity extends Activity {
    public FocusScaleListener focusScaleListener = new FocusScaleListener();
    private JSONObject hotelMessage = null;
    public CommonData commonData = CommonData.getInstance();
    private String TAG = "BaseActivity";


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

//    登录
    public void login(String serverAddress,String roomNumber,String tenant){
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                BackstageHttp.getInstance().loginSystem(serverAddress,roomNumber,tenant);
                latch.countDown();
            }
        }).start();
//
        try {
            latch.await(); // 等待请求完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


//    拼接websocket路径
    public String getWebSocketUrl(String serverAddress){
        String token = BackstageHttp.getInstance().getToken();
        if (serverAddress.startsWith("http://")) {
            return serverAddress.replace("http://", "ws://") + "/infra/ws?token=" + token;
        } else if (serverAddress.startsWith("https://")) {
            return serverAddress.replace("https://", "ws://") + "/infra/ws?token=" + token;
        }
        return null;
    }

//    处理接收到websocket信息事件
    public void initWebSocket(Context context, WebSocketClient webSocketClient, String url){
        webSocketClient = new WebSocketClient(url);
        webSocketClient.setMessageCallback(new WebSocketClient.MessageCallback() {
            @Override
            public void onMessageCallback(String data) {
                try{
                    JSONObject jsonObject = new JSONObject(data);
                    String type = jsonObject.getString("type");
                    if (type.equals("insert-notice")){
//                        由于websocket二级是字符串所以先拿字符串然后转成json
                        String strContent = jsonObject.getString("content");
                        JSONObject contentObject = new JSONObject(strContent);
//                        获取播放信息
                        String content= contentObject.getString("content");
//                        配置信息
                        JSONObject configData = contentObject.getJSONObject("configData");
                        int webType = configData.getInt("type");
                        long webSecond = configData.getLong("second");
                        Intent intent = new Intent(context, SocketNoticeActivity.class);
                        intent.putExtra("url",content);
                        intent.putExtra("type",webType);
                        intent.putExtra("second",webSecond);
                        startActivity(intent);
                    }
                }catch (JSONException e){
                    Log.e(TAG, "onMessageCallback: " + data, e );
                }
            }
        });
    }


}
