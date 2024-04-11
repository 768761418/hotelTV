package com.qb.hotelTV.Activity;

import static com.qb.hotelTV.Utils.TimeUtil.getCurrentDateTime;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Adaptor.ApkAdaptor;
import com.qb.hotelTV.Adaptor.TvChannelAdaptor;
import com.qb.hotelTV.Adaptor.common.CommonAdapter;
import com.qb.hotelTV.Adaptor.common.CommonViewHolder;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Http.LocationHttp;
import com.qb.hotelTV.Utils.PermissionUtils;
import com.qb.hotelTV.databinding.LayoutIndexBinding;
import com.qb.hotelTV.module.TvChooseModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingDeque;

public class IndexActivity extends BaseActivity {
    private Handler handler = new Handler();
    LayoutIndexBinding layoutIndexBinding;
    private String TAG = IndexActivity.class.getSimpleName();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    double latitude,longitude;

    private Integer GEO=0,WEATHER=0,TEXT=0,APK=0,ROOM_MESSAGE=0,HOTEL_MESSAGE=0;

    private String geo,weather,strRoomName,
            strWifiName,strWifiPassword,strDeskNumber,
            strHotelName,strHotelLogo,strHotelBg,
            strTvText,strTvTextColor,serverAddress,tenant;

    ArrayList<ApkModel> apkList = new ArrayList<>();
    ArrayList<VideoModel> videoModels  = new ArrayList<>();
    ProgressDialog progressDialog;
    private static final String KEY_SERVER_ADDRESS = "server_address";
    private static final String KEY_ROOM_NUMBER = "room_number";
    private static final String KEY_TENANT = "tenant";
    private static final String PREFS_NAME = "HotelTV";
    TvChooseModule tvChooseModule = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutIndexBinding = DataBindingUtil.setContentView(this, R.layout.layout_index);
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
            String roomNumber = sharedPreferences.getString(KEY_ROOM_NUMBER, "");
            tenant  = sharedPreferences.getString(KEY_TENANT,"");
            // 使用服务器地址和房间号
            // ...
            initUI(roomNumber);
        }

        chooseTv();


    }


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
    protected void onDestroy() {
        // 移除所有未执行的任务，避免内存泄漏
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }



    private void chooseTv(){
        layoutIndexBinding.indexVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BackstageHttp.getInstance().getTvChannel(serverAddress, tenant, new BackstageHttp.TvChannelCallback() {
                    @Override
                    public void onTvChannelResponse(ArrayList<VideoModel> list) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                        TvChannelAdaptor tvChannelAdaptor = new TvChannelAdaptor(IndexActivity.this, list, new TvChooseModule.SwitchListener() {
                                    @Override
                                    public void onSwitch(String url) {
                                        layoutIndexBinding.indexVideo.setVideoURI(Uri.parse(url));
                                        layoutIndexBinding.indexVideo.start();
                                        if(tvChooseModule!=null){
                                            tvChooseModule.dismiss();
                                        }


                                    }
                                });
                                tvChooseModule = new TvChooseModule(IndexActivity.this, tvChannelAdaptor);
                                tvChooseModule.show();
                            }
                        });
                    }

                    @Override
                    public void onTvChannelFailure(int code, String msg) {
                    }


                });


            }
        });

    }


    private void initUI(String roomNumber){

//        获取时间
        startUpdateTask();
        initAdapter();


//        获取经纬度
        getLocation();
        // 将经纬度保留两位小数并合成字符串
        String locationString = String.format("%.2f,%.2f",  longitude, latitude);
//        请求多个接口获取数据
        getDataFromHttp(serverAddress,roomNumber,locationString,tenant);

        showProgressDialog();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(GEO == 1 && WEATHER == 1 &&ROOM_MESSAGE ==1 && TEXT==1){
                    dismissProgressDialog();
                    timer.cancel();
//                    在主线程修改组件
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layoutIndexBinding.indexName.setText(strHotelName);
                            Glide.with(IndexActivity.this)
                                    .load(strHotelLogo)
                                    .error(R.drawable.img)
                                    .into(layoutIndexBinding.indexLogo);
                            Glide.with(IndexActivity.this)
                                    .load(strHotelBg)
                                    .error(R.drawable.img)
                                    .into(layoutIndexBinding.indexBackground);

                            layoutIndexBinding.indexSky.setText(geo + "  " + weather);
                            layoutIndexBinding.indexRoomName.setText(strRoomName);
                            layoutIndexBinding.indexWifiName.setText(strWifiName);
                            layoutIndexBinding.indexWifiPassword.setText(strWifiPassword);
                            layoutIndexBinding.indexDeskNumber.setText(strDeskNumber);
//                            apk的列表
                            layoutIndexBinding.indexApk.setAdapter(apkAdaptor);
                            layoutIndexBinding.indexTvText.setText(strTvText);
//                            layoutIndexBinding.indexTvText.setTextColor(Color.parseColor(strTvTextColor));
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask,0,1000);

    }


    // 创建一个新的 Runnable 对象，用于更新日期和时间
    private void startUpdateTask() {
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                // 获取当前日期和时间
                String currentDateTimeString = getCurrentDateTime();

                // 分割日期和时间
                String[] parts = currentDateTimeString.split(" ");
                String datePart = parts[0];
                String timePart = parts[1];
                // 更新 TextView 的文本内容为当前日期和时间的各部分
                layoutIndexBinding.indexDate.setText(datePart);
                layoutIndexBinding.indexTime.setText(timePart);

                // 间隔一段时间后再次执行任务（这里设置为每秒更新一次）
                handler.postDelayed(this, 1000);
            }
        };

        // 执行第一次任务
        handler.post(updateTask);
    }






    CommonAdapter<ApkModel> apkAdaptor;
    CommonAdapter<VideoModel> videoModelCommonAdapter;
    private void initAdapter(){
        apkAdaptor = new CommonAdapter<ApkModel>(IndexActivity.this,apkList,R.layout.item_apk) {
            @Override
            public void bindData(CommonViewHolder holder, ApkModel data, int position) {
                holder.setText(R.id.apk_name,apkList.get(position).getName());
                Glide.with(IndexActivity.this)
                        .load(apkList.get(position).getLogoUrl())
                        .error(R.color.white)
                        .into((ImageView) holder.getView(R.id.apk_logo));
                int bgColor = Color.parseColor(apkList.get(position).getBackgroundColor());
                holder.getView(R.id.apk_item_view).setBackgroundColor(bgColor);
                holder.setCommonClickListener(new CommonViewHolder.OnCommonItemEventListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //判断packagenames是否存在
                        gotoOtherApp(data.getSchemeUrl());

                    }

                    @Override
                    public void onItemLongClick(int viewId, int position) {

                    }
                });
            }
        };

        videoModelCommonAdapter = new CommonAdapter<VideoModel>(this,videoModels,R.layout.item_tv) {
            @Override
            public void bindData(CommonViewHolder holder, VideoModel data, int position) {
                holder.setText(R.id.tv_name,data.getStreamName());
                holder.setCommonClickListener(new CommonViewHolder.OnCommonItemEventListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //TODO
                    }

                    @Override
                    public void onItemLongClick(int viewId, int position) {

                    }
                });
            }
        };
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
                latitude = 0;
                longitude = 0;
                return;
            }
            do{
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();
                Log.d(TAG, "Latitude: " + latitude + ", Longitude: " + longitude);
                // 在此处处理获取到的经纬度信息
            } while(lastKnownLocation == null);
        } catch (SecurityException e) {
            // 处理没有定位权限的情况
            Log.e(TAG, "Location permission denied: " + e.getMessage());
        }
    }
    private void gotoOtherApp(String packageName){
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            // 应用未安装或包名无效
            Toast.makeText(this, "应用未安装", Toast.LENGTH_SHORT).show();
        }
    }

    private void getDataFromHttp(String serverAddress,String roomNumber,String locationString,String tenant){

        if (HOTEL_MESSAGE == 0){
            BackstageHttp.getInstance().getHotelMessage(serverAddress, tenant, new BackstageHttp.HotelMessageCallback() {
                @Override
                public void onHotelMessageResponse(String hotelName, String hotelLogo, String hotelBackground) {
                    strHotelName = hotelName;
                    strHotelLogo = hotelLogo;
                    strHotelBg = hotelBackground;
                    HOTEL_MESSAGE = 1;
                }

                @Override
                public void onHotelMessageFailure(int code, String msg) {
                    HOTEL_MESSAGE = 1;
                }
            });
        }


//        请求地址
        if (GEO == 0){
            LocationHttp.getInstance().getGeo(locationString, new LocationHttp.LocationHttpCallback() {
                @Override
                public void onResponse(String responseData) {
                    geo = responseData;
                    GEO = 1;
                }

                @Override
                public void onFailure(String failName) {
                    geo = "";
                    GEO = 1;
                }
            });
        }

//        请求天气
        if (WEATHER == 0){
            LocationHttp.getInstance().getWeather(locationString, new LocationHttp.LocationHttpCallback() {
                @Override
                public void onResponse(String responseData) {
                    weather = responseData;
                    WEATHER = 1;
                    Log.d(TAG, "++" + weather);
                }

                @Override
                public void onFailure(String failName) {
                    weather = "";
                    WEATHER = 1;
                    Log.d(TAG, "++" + weather);
                }
            });
        }

//        请求滚动栏
        if (TEXT == 0){
            BackstageHttp.getInstance().getTvText(serverAddress, tenant, new BackstageHttp.TvTextCallback() {
                @Override
                public void onTvTextResponse(String tvText, String tvTextColor) {
                    strTvText = tvText;
                    strTvTextColor = tvTextColor;
                    Log.d(TAG, "111000" +strTvText + tvTextColor);
                    TEXT = 1;
                }

                @Override
                public void onTvTextFailure(int code, String msg) {
                    TEXT = 1;
                }
            });
        }

//        请求房间信息
        if (ROOM_MESSAGE == 0){
            BackstageHttp.getInstance().getRoomMessage(serverAddress, roomNumber, tenant,new BackstageHttp.RoomMessageCallback() {
                @Override
                public void onRoomMessageResponse(int id, String roomName, String wifiPassword, String frontDeskPhone) {
                    Log.d(TAG, "BackstageHttp" + id + "/" + roomName + "/" + wifiPassword + "/" + frontDeskPhone);
                    strRoomName = roomName;
                    strWifiName = roomNumber + "_" +roomName;
                    strWifiPassword = wifiPassword;
                    strDeskNumber = frontDeskPhone;
                    ROOM_MESSAGE =1;
                }

                @Override
                public void onRoomMessageFailure(int code, String msg) {
                    if(code == -1){
                        Log.d(TAG, "onRoomMessageFailure: " + msg);
                        ROOM_MESSAGE = 1 ;
                    }
                }


            });
        }

//        请求apk列表
        if (APK == 0){
            BackstageHttp.getInstance().getApk(serverAddress, tenant,new BackstageHttp.ApkCallback() {
                @Override
                public void onApkResponse(ArrayList<ApkModel> apkModelArrayList) {
                    apkList.addAll(apkModelArrayList);
                    apkAdaptor.notifyDataSetChanged();
                    APK = 1;
                }

                @Override
                public void onApkFailure(int code, String msg) {
                    Log.d(TAG, "onRoomMessageFailure: ");
                    APK = 1;
                }
            });
        }


    }

    // 显示等待对话框
    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..."); // 设置等待消息
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // 设置进度条样式为圆形旋转
        progressDialog.setCancelable(false); // 设置对话框不可取消
        progressDialog.show(); // 显示对话框
    }

    // 隐藏等待对话框
    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss(); // 关闭对话框
        }
    }


//    输入框
    private void showInputDialog() {
        // 创建输入对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("请配置服务器信息");

        // 设置输入框
        final EditText serverAddressInput = new EditText(this);
        final EditText roomNumberInput = new EditText(this);
        final EditText tenantInput = new EditText(this);
        serverAddressInput.setHint("服务器地址");
        roomNumberInput.setHint("房间号");
        tenantInput.setHint("分组");
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(serverAddressInput);
        layout.addView(roomNumberInput);
        layout.addView(tenantInput);
        builder.setView(layout);

        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                serverAddress = serverAddressInput.getText().toString();
                String roomNumber = roomNumberInput.getText().toString();
                tenant = tenantInput.getText().toString();
                // 保存服务器地址和房间号到 SharedPreferences
                saveServerAddressAndRoomNumber(serverAddress, roomNumber,tenant);
                initUI(roomNumber);
            }
        });

        // 显示对话框
        builder.show();
    }

    private void saveServerAddressAndRoomNumber(String serverAddress, String roomNumber,String tenant) {
        editor = sharedPreferences.edit();
        editor.putString(KEY_SERVER_ADDRESS, serverAddress);
        editor.putString(KEY_ROOM_NUMBER, roomNumber);
        editor.putString(KEY_TENANT,tenant);
        editor.putBoolean("isFirstRun", false); // 标记不是第一次运行
        editor.apply();
    }
}