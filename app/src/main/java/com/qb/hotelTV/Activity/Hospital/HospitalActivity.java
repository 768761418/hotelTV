package com.qb.hotelTV.Activity.Hospital;

import static com.qb.hotelTV.Utils.TimeUtil.getCurrentDateTime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.qb.hotelTV.Activity.AppActivity;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Activity.HotelActivity;
import com.qb.hotelTV.Activity.IndexActivity;
import com.qb.hotelTV.Activity.VideoActivity;
import com.qb.hotelTV.Const;
import com.qb.hotelTV.Handler.CrashHandler;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Http.LocationHttp;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Model.HotelListModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.PermissionUtils;
import com.qb.hotelTV.databinding.LayoutHospitalBinding;
import com.qb.hotelTV.huibuTv.MainActivity;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HospitalActivity extends BaseActivity {
    private  String TAG = "HospitalActivity";
    LayoutHospitalBinding layoutHospitalBinding;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Handler handler = new Handler();
    private ArrayList<HotelListModel> hotelList = new ArrayList<>();

    private String geo,weather,strRoomName,
            strWifiName,strWifiPassword,strDeskNumber,
            strHotelName,strHotelLogo,strHotelBg,strResourceUrl,strDetail, strVideoUrl,
            strTvText,strTvTextColor;
    private boolean GEO=false,WEATHER=false,TEXT=false,HOTEL_LIST=false,ROOM_MESSAGE=false,HOTEL_MESSAGE=false,TV_CHANNEL =false;
//    请求接口的请求头
    private String serverAddress,roomNumber,tenant;
//    经纬度
    private String locationString ;
    private SimpleExoPlayer player ;
    FocusScaleListener focusScaleListener = new FocusScaleListener();

    private static final String KEY_SERVER_ADDRESS = "server_address";
    private static final String KEY_ROOM_NUMBER = "room_number";
    private static final String KEY_TENANT = "tenant";
    private static final String PREFS_NAME = "Hospital";


    //    请求权限
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.REQUEST_CODE){
            for (int i = 0; i < grantResults.length; i++) {
//                如果没请求成功，在这写
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onRequestPermissionsResult: "+permissions[i] +":111");
                }
//                如果请求成功在这写
                else {
                    Log.d(TAG, "onRequestPermissionsResult: " + permissions[i] );
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 当 Activity 失去焦点时，暂停视频播放
        if (player != null){
            player.pause();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null){
            // 当 Activity 重新获得焦点时，继续播放视频
            player.play();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutHospitalBinding = DataBindingUtil.setContentView(this, R.layout.layout_hospital);
//        闪退日志
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
//        请求权限
        PermissionUtils permissionUtils = new PermissionUtils();
        permissionUtils.checkPermission(this);
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isFirstRun", true);
        if (isFirstRun) {
            // 如果是第一次进入，则显示输入对话框
            showInputDialog();
        } else {
            // 如果不是第一次进入，则直接使用保存的服务器地址和房间号
            serverAddress = sharedPreferences.getString(KEY_SERVER_ADDRESS, "");
            roomNumber = sharedPreferences.getString(KEY_ROOM_NUMBER, "");
            tenant  = sharedPreferences.getString(KEY_TENANT,"");
            Log.d(TAG, "serverAddress: " +serverAddress);
            Log.d(TAG, "roomNumber: " + roomNumber);
            Log.d(TAG, "tenant: " +tenant);
            // 使用服务器地址和房间号
            // ...
            initUI();
        }

    }



    private void initUI(){
        getLocation();
        startUpdateTask();
//        请求多个接口获取数据
        getGeoAndWeather(locationString);
        getDataFromHttp();

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (GEO&&WEATHER&&HOTEL_MESSAGE){
                    //        主线程修改组件
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timer.cancel();
                            layoutHospitalBinding.indexSky.setText(weather);

                            if (strResourceUrl != null && !strResourceUrl.equals("")){
                                player = new SimpleExoPlayer.Builder(HospitalActivity.this).build();
//                                绑定player
                                layoutHospitalBinding.hospitalTv.setPlayer(player);
                                // 隐藏控制面板
                                layoutHospitalBinding.hospitalTv.setUseController(false);
//                                设置循环播放
                                player.setRepeatMode(Player.REPEAT_MODE_ALL);
                                MediaItem mediaItem = MediaItem.fromUri(strResourceUrl);
                                player.setMediaItem(mediaItem);
                                player.prepare();
                                player.play();
                            }
                            if (strHotelLogo!= null){
                                Glide.with(HospitalActivity.this)
                                        .load(strHotelLogo)
                                        .error(R.drawable.img)
                                        .into(layoutHospitalBinding.hospitalLogo);
                            }

//                            设置背景
                            if (strHotelBg != null){
                                Glide.with(HospitalActivity.this)
                                        .load(strHotelBg)
                                        .error(R.drawable.app_bg)
                                        .into(layoutHospitalBinding.hospitalBackground);

                            }

                            layoutHospitalBinding.indexRoomNumber.setText(roomNumber);


                            if (strTvText == null || strTvText.equals("")){
                                layoutHospitalBinding.hospitalTvText.setVisibility(View.GONE);
                            }else {
                                layoutHospitalBinding.hospitalTvText.setVisibility(View.VISIBLE);
                                layoutHospitalBinding.hospitalTvText.setText(strTvText);
//                                int color = Color.parseColor(strTvTextColor);
//                                layoutHospitalBinding.hospitalTvText.setTextColor(color);
                            }


                        }
                    });

                }
            }
        };
        timer.schedule(timerTask,0,1000);


        clickEvent();
        focusChange();


    }


    private void showInputDialog() {
        layoutHospitalBinding.indexInput.setVisibility(View.VISIBLE);
//        layoutIndexBinding.tvImage.setEnabled(false);
        layoutHospitalBinding.inputSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 在点击事件中获取按下的按钮的 keycode
                int keyCode; // 初始化为未知的 keycode
                KeyEvent keyEvent = (KeyEvent) view.getTag(); // 从 view 的 tag 中获取 KeyEvent 对象
                if (keyEvent != null) {
                    keyCode = keyEvent.getKeyCode(); // 获取按下的按钮的 keycode
                    Toast.makeText(HospitalActivity.this, "!!!:" + keyCode, Toast.LENGTH_SHORT).show();
                }
                serverAddress = layoutHospitalBinding.inputServerAddress.getText().toString();
                roomNumber = layoutHospitalBinding.inputRoomNumber.getText().toString();
                tenant = layoutHospitalBinding.inputTenant.getText().toString();
                Log.d(TAG, "tenant:xxx" + tenant);
                // 保存服务器地址和房间号到 SharedPreferences
                saveServerAddressAndRoomNumber(serverAddress, roomNumber,tenant);
                initUI();
                layoutHospitalBinding.indexInput.setVisibility(View.GONE);
//                layoutIndexBinding.tvImage.setEnabled(true);

            }
        });
    }
    private void saveServerAddressAndRoomNumber(String serverAddress, String roomNumber,String tenant) {
        editor = sharedPreferences.edit();
        editor.putString(KEY_SERVER_ADDRESS, serverAddress);
        editor.putString(KEY_ROOM_NUMBER, roomNumber);
        editor.putString(KEY_TENANT,tenant);
        editor.putBoolean("isFirstRun", false); // 标记不是第一次运行
        editor.apply();
    }

    //    获取坐标
    private void getLocation(){
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
                return;
            }
            do{
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                // 将经纬度保留两位小数并合成字符串
                locationString = String.format("%.2f,%.2f",  longitude, latitude);
                Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
                // 在此处处理获取到的经纬度信息
            } while(lastKnownLocation == null);
        } catch (SecurityException e) {
            // 处理没有定位权限的情况
            Log.e(TAG, "Location permission denied: " + e.getMessage());
        }
    }

    // 创建一个新的 Runnable 对象，用于更新日期和时间
    private void startUpdateTask() {
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                // 获取当前日期和时间
                String currentDateTimeString = getCurrentDateTime(1);
                // 分割日期和时间
                String[] parts = currentDateTimeString.split(" ");
                String datePart = parts[0];
                String timePart = parts[1];
                // 更新 TextView 的文本内容为当前日期和时间的各部分
                layoutHospitalBinding.indexDate.setText(datePart);
                layoutHospitalBinding.indexTime.setText(timePart);

                // 间隔一段时间后再次执行任务（这里设置为每秒更新一次）
                handler.postDelayed(this, 1000);
            }
        };

        // 执行第一次任务
        handler.post(updateTask);
    }

    //    请求天气和温度
    private  void getGeoAndWeather(String locationString){
//        请求地址
        if (!GEO){
            LocationHttp.getInstance().getGeo(locationString, new LocationHttp.LocationHttpCallback() {
                @Override
                public void onResponse(String responseData) {
                    geo = responseData;
                    GEO = true;
                }

                @Override
                public void onFailure(String failName) {
                    geo = "";
                    GEO = true;
                }
            });
        }

//        请求天气
        if (!WEATHER){
            LocationHttp.getInstance().getWeather(locationString, new LocationHttp.LocationHttpCallback() {
                @Override
                public void onResponse(String responseData) {
                    weather = responseData;
                    WEATHER = true;
                    Log.d(TAG, "++" + weather);
                }

                @Override
                public void onFailure(String failName) {
                    weather = "";
                    WEATHER = true;
                    Log.d(TAG, "++" + weather);
                }
            });
        }
    }


    private void clickEvent(){
        layoutHospitalBinding.hospitalModule0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layoutHospitalBinding.hospitalModule0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layoutHospitalBinding.hospitalModule1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layoutHospitalBinding.hospitalModule2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layoutHospitalBinding.hospitalModule3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layoutHospitalBinding.hospitalModule4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layoutHospitalBinding.hospitalModule5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        layoutHospitalBinding.hospitalModule6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void focusChange(){
//        layoutIndexBinding.tvImage.requestFocus();
//        layoutIndexBinding.tvImage.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalBinding.hospitalModule0.requestFocus();
        layoutHospitalBinding.hospitalModule0.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalBinding.hospitalModule1.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalBinding.hospitalModule2.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalBinding.hospitalModule3.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalBinding.hospitalModule4.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalBinding.hospitalModule5.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalBinding.hospitalModule6.setOnFocusChangeListener(focusScaleListener);

    }


    //    从接口获取数据
    private void getDataFromHttp(){
        if (!HOTEL_MESSAGE){
            BackstageHttp.getInstance().getHotelMessage(serverAddress, tenant, new BackstageHttp.HotelMessageCallback() {
                @Override
                public void onHotelMessageResponse(String hotelName, String hotelLogo, String hotelBackground,String resourceUrl,String detail, String videoUrl) {
                    strHotelName = hotelName;
                    strHotelLogo = hotelLogo;
                    strHotelBg = hotelBackground;
                    strResourceUrl = resourceUrl;
                    strDetail = detail;
                    strVideoUrl = videoUrl;
                    HOTEL_MESSAGE = true;
                }
                @Override
                public void onHotelMessageFailure(int code, String msg) {
                    strHotelName = "";
                    strHotelBg = "";
                    HOTEL_MESSAGE = true;
                }
            });
        }
//        请求滚动栏
        if (!TEXT){
            BackstageHttp.getInstance().getTvText(serverAddress, tenant, new BackstageHttp.TvTextCallback() {
                @Override
                public void onTvTextResponse(String tvText, String tvTextColor) {
                    strTvText = tvText;
                    strTvTextColor = tvTextColor;
                    TEXT = true;
                }

                @Override
                public void onTvTextFailure(int code, String msg) {
                    strTvText = "";
                    strTvTextColor = "";
                    TEXT = true;
                }
            });
        }

//        请求房间信息
        if (!ROOM_MESSAGE){
            BackstageHttp.getInstance().getRoomMessage(serverAddress, roomNumber, tenant,new BackstageHttp.RoomMessageCallback() {
                @Override
                public void onRoomMessageResponse(int id, String roomName, String wifiPassword, String frontDeskPhone) {
                    Log.d(TAG, "BackstageHttp" + id + "/" + roomName + "/" + wifiPassword + "/" + frontDeskPhone);
                    if (roomName.equals("")){
                        strRoomName = Const.MSG_ROOM_NOT_EXIST;
                        strWifiName = Const.MSG_ROOM_NOT_EXIST;
                    }else {
                        strRoomName = roomName;
                        strWifiName = roomNumber + "_" +roomName;
                    }

                    strWifiPassword = wifiPassword;
                    strDeskNumber = frontDeskPhone;
                    ROOM_MESSAGE =true;
                }
                @Override
                public void onRoomMessageFailure(int code, String msg) {
                    if(code == -1){
                        strRoomName = "";
                        strWifiName = roomNumber + "_" ;
                        strWifiPassword = "";
                        strDeskNumber = "";
                        ROOM_MESSAGE = true ;
                    }
                }
            });
        }

        if (!HOTEL_LIST){
            BackstageHttp.getInstance().getHotelList(serverAddress, tenant, new BackstageHttp.HotelListCallBack() {
                @Override
                public void onHotelListResponse(ArrayList<HotelListModel> hotelListModels) {
                    try {
                        hotelList.clear();
                        hotelList.addAll(hotelListModels);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                for (int i = 0; i < hotelList.size(); i++) {
                                    Log.d(TAG, "onApkResponse: " + hotelList.get(i).getName());
                                    Log.d(TAG, "onApkResponse: " + hotelList.get(i).getBackgroundUrl());
                                    Log.d(TAG, "onApkResponse: " + hotelList.get(i).getPicUrl());

                                    //获取到每一个item的layout替换掉图片和文字和跳转地址
                                    LinearLayout item = (LinearLayout) ((LinearLayout) layoutHospitalBinding.hospitalMainBottomLayout.getChildAt(i)).getChildAt(0);

                                    ImageView img  = item.findViewById(R.id.item_img);

                                    Glide.with(HospitalActivity.this)
                                            .load(hotelList.get(i).getPicUrl())
                                            .error(R.color.black)
                                            .into(img);

                                    ((TextView)item.findViewById(R.id.item_text)).setText(hotelList.get(i).getName());


                                    SimpleTarget<Drawable>  simpleTarget = new SimpleTarget<Drawable>() {
                                        @Override
                                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                            item.setBackground(resource);
                                        }
                                    };
                                    Glide.with(HospitalActivity.this)
                                            .load(hotelList.get(i).getBackgroundUrl())
                                            .error(R.color.white)
                                            .into(simpleTarget);
                                    int finalI  = i;
                                    switch (hotelList.get(i).getType()){
                                        case 10:
                                            item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(HospitalActivity.this, MainActivity.class);
                                                    startActivity(intent);
                                                }
                                            });
                                            break;
                                        case 9:
                                            item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(HospitalActivity.this, AppActivity.class);
                                                    intent.putExtra("bg",strHotelBg);
                                                    intent.putExtra("serverAddress",serverAddress);
                                                    intent.putExtra("tenant",tenant);
                                                    intent.putExtra("title",hotelList.get(finalI).getName());
                                                    startActivity(intent);
                                                }
                                            });
                                            break;
                                        case 0:

                                            item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(HospitalActivity.this, HospitalWebActivity.class);
                                                    intent.putExtra("bg",strHotelBg);
                                                    intent.putExtra("serverAddress",serverAddress);
                                                    intent.putExtra("tenant",tenant);
                                                    intent.putExtra("roomNumber",roomNumber);
                                                    intent.putExtra("weather",weather);

                                                    intent.putExtra("title",hotelList.get(finalI).getName());
                                                    intent.putExtra("id",hotelList.get(finalI).getId());
                                                    intent.putExtra("logo",strHotelLogo);


                                                    Log.d(TAG, "detail: " +  hotelList.get(finalI).getId());
                                                    intent.putExtra("detail",strDetail);
                                                    startActivity(intent);
                                                }
                                            });
                                            break;
                                        case 2:
                                            item.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Intent intent = new Intent(HospitalActivity.this, VideoActivity.class);
                                                    intent.putExtra("bg",strHotelBg);
                                                    intent.putExtra("serverAddress",serverAddress);
                                                    intent.putExtra("tenant",tenant);
                                                    intent.putExtra("title",hotelList.get(finalI).getName());
                                                    Log.d(TAG, "strVideoUrl: " +strVideoUrl);
                                                    intent.putExtra("videoUrl",strVideoUrl);
                                                    startActivity(intent);
                                                }
                                            });
                                            break;


                                    }

                                }
                            }
                        });
                    }catch (Exception e){

                    }

                }

                @Override
                public void onHotelLIstFailure(int code, String msg) {

                }
            });
        }






    }

}
