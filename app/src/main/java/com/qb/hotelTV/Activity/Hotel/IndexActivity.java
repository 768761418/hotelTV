package com.qb.hotelTV.Activity.Hotel;
import static com.qb.hotelTV.Utils.LoadUtils.dismissProgressDialog;
import static com.qb.hotelTV.Utils.LoadUtils.showProgressDialog;
import static com.qb.hotelTV.Utils.TimeUtil.getCurrentDateTime;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.qb.hotelTV.Activity.AppActivity;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Adaptor.common.CommonAdapter;
import com.qb.hotelTV.Const;
import com.qb.hotelTV.Handler.CrashHandler;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.Model.HotelListModel;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Http.LocationHttp;
import com.qb.hotelTV.Setting.ProgressDialogSetting;
import com.qb.hotelTV.Utils.PermissionUtils;
import com.qb.hotelTV.databinding.LayoutIndexBinding;
import com.qb.hotelTV.huibuTv.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class IndexActivity extends BaseActivity {
    private Handler handler = new Handler();
    LayoutIndexBinding layoutIndexBinding;
    private final String  TAG = IndexActivity.class.getSimpleName();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
//    经纬度
    String locationString;
    int current_channel;

//    接口是否运行状态码
    private Integer GEO=0,WEATHER=0,TEXT=0,HOTEL_LIST=0,ROOM_MESSAGE=0,HOTEL_MESSAGE=0,TV_CHANNEL =0;
//    视频是否全屏
    private boolean VIDEO_STATUS = false;
    private boolean FULL_SCREEN_SIDEBAR = false;

    private String geo,weather,strRoomName,
            strWifiName,strWifiPassword,strDeskNumber,
            strHotelName,strHotelLogo,strHotelBg,strResourceUrl,strDetail, strVideoUrl,
            strTvText,strTvTextColor;

//    从SharedPreferences中获取的数据
    private String serverAddress,tenant,roomNumber;

    private ArrayList<HotelListModel> hotelList = new ArrayList<>();
    ProgressDialog progressDialog;
    private SimpleExoPlayer player ;
//    焦点选中动画
    FocusScaleListener focusScaleListener = new FocusScaleListener();

    private static final String KEY_SERVER_ADDRESS = "server_address";
    private static final String KEY_ROOM_NUMBER = "room_number";
    private static final String KEY_TENANT = "tenant";
    private static final String PREFS_NAME = "HotelTV";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutIndexBinding = DataBindingUtil.setContentView(this, R.layout.layout_index);
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

//        组件动画
        focusChange();
//        openClick();
    }

    private void openClick(){
        layoutIndexBinding.apk1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        layoutIndexBinding.apk2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        layoutIndexBinding.apk3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        layoutIndexBinding.apk4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, VideoActivity.class);
                intent.putExtra("bg",strHotelBg);
                intent.putExtra("serverAddress",serverAddress);
                intent.putExtra("tenant",tenant);
                Log.d(TAG, "strVideoUrl: " +strVideoUrl);
                intent.putExtra("videoUrl",strVideoUrl);
                startActivity(intent);
            }
        });
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
    protected void onDestroy() {
        // 移除所有未执行的任务，避免内存泄漏
        handler.removeCallbacksAndMessages(null);
        if (player != null){
            player.release();
        }
        super.onDestroy();
    }


    private void initUI(){
//        获取时间
        startUpdateTask();
        startUpdateTvTextTask();
        initAdapter();
//        获取经纬度
        locationString = getLocation();

//        请求多个接口获取数据
        getGeoAndWeather(locationString);
        try {
            getDataFromHttp();
        }catch (Exception e){
            Log.e(TAG, "initUI: ", e);
        }


        showProgressDialog(IndexActivity.this, ProgressDialogSetting.loading);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(GEO == 1 && WEATHER == 1 &&ROOM_MESSAGE ==1 && TEXT==1 ){
                    dismissProgressDialog();
                    timer.cancel();
//                    在主线程修改组件
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (strResourceUrl != null && !strResourceUrl.equals("")){
                                player = new SimpleExoPlayer.Builder(IndexActivity.this).build();
//                                绑定player
                                layoutIndexBinding.indexTv.setPlayer(player);
                                // 隐藏控制面板
                                layoutIndexBinding.indexTv.setUseController(false);
//                                设置循环播放
                                player.setRepeatMode(Player.REPEAT_MODE_ALL);
                                MediaItem mediaItem = MediaItem.fromUri(strResourceUrl);
                                player.setMediaItem(mediaItem);
                                player.prepare();
                                player.play();
                            }

//                            设置名称
                            if (strHotelName == null || strHotelName.equals("")){
                                layoutIndexBinding.indexName.setText(Const.MSG_NETWORK_ERR);
                            }else {
                                layoutIndexBinding.indexName.setText(strHotelName);
                            }
//                            设置logo
                            if (strHotelLogo != null){
                                Glide.with(IndexActivity.this)
                                        .load(strHotelLogo)
                                        .error(R.drawable.img)
                                        .into(layoutIndexBinding.indexLogo);
                            }
//                            设置背景
                            if (strHotelBg != null){
                                Glide.with(IndexActivity.this)
                                        .load(strHotelBg)
                                        .error(R.drawable.app_bg)
                                        .into(layoutIndexBinding.indexBackground);

                            }

//                            设置天气
                            layoutIndexBinding.indexSky.setText(geo + "  " + weather);
//                            设置房间信息
                            layoutIndexBinding.indexRoomName.setText(strRoomName);
                            layoutIndexBinding.indexWifiName.setText(strWifiName);
                            layoutIndexBinding.indexWifiPassword.setText(strWifiPassword);
                            layoutIndexBinding.indexDeskNumber.setText(strDeskNumber);
//                            apk的列表
//                            layoutIndexBinding.indexApk.setAdapter(apkAdaptor);
//                            设置滚动栏
                            if (strTvText == null || strTvText.equals("")){
                                layoutIndexBinding.indexTvText.setVisibility(View.GONE);
                            }else {
                                layoutIndexBinding.indexTvText.setVisibility(View.VISIBLE);
                                layoutIndexBinding.indexTvText.setText(strTvText);
                                int color = Color.parseColor(strTvTextColor);
                                layoutIndexBinding.indexTvText.setTextColor(color);
                            }
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

    private void startUpdateTvTextTask(){
        Runnable startUpdateTvTextTask = new Runnable() {
            @Override
            public void run() {
                BackstageHttp.getInstance().getTvText(serverAddress, tenant, new BackstageHttp.TvTextCallback() {
                    @Override
                    public void onTvTextResponse(String tvText, String tvTextColor,int code) {
                        updateTvText(tvText);
                    }

                    @Override
                    public void onTvTextFailure(int code, String msg) {
                        updateTvText("");
                    }
                });


                handler.postDelayed(this, 60*1000);
            }
        };
        handler.post(startUpdateTvTextTask);
    }

    private void updateTvText(final String tvText){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (tvText == null || tvText.equals("")||strTvTextColor==null||strTvTextColor.equals("")){
                    layoutIndexBinding.indexTvText.setVisibility(View.GONE);
                }else {
                    layoutIndexBinding.indexTvText.setVisibility(View.VISIBLE);
                    layoutIndexBinding.indexTvText.setText(tvText);
                    int color = Color.parseColor(strTvTextColor);
                    layoutIndexBinding.indexTvText.setTextColor(color);
                }
            }
        });
    }




    CommonAdapter<ApkModel> apkAdaptor;
    CommonAdapter<VideoModel> videoModelAdapter;
    private void initAdapter(){
//        apkAdaptor = new CommonAdapter<ApkModel>(IndexActivity.this,apkList,R.layout.item_apk) {
//            @Override
//            public void bindData(CommonViewHolder holder, ApkModel data, int position) {
//                holder.setText(R.id.apk_name,apkList.get(position).getName());
//                Glide.with(IndexActivity.this)
//                        .load(apkList.get(position).getLogoUrl())
//                        .error(R.color.white)
//                        .into((ImageView) holder.getView(R.id.apk_logo));
//                int bgColor = Color.parseColor(apkList.get(position).getBackgroundColor());
//                holder.getView(R.id.apk_item_view).setBackgroundColor(bgColor);
//                holder.setCommonClickListener(new CommonViewHolder.OnCommonItemEventListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        //判断packagenames是否存在
//                        gotoOtherApp(data.getSchemeUrl());
//
//                    }
//
//                    @Override
//                    public void onItemLongClick(int viewId, int position) {
//
//                    }
//                });
//            }
//        };

    }





//    焦点切换动画
    private void focusChange(){
//        layoutIndexBinding.tvImage.requestFocus();
//        layoutIndexBinding.tvImage.setOnFocusChangeListener(focusScaleListener);
        layoutIndexBinding.apk1.requestFocus();
        layoutIndexBinding.apk1.setOnFocusChangeListener(focusScaleListener);
        layoutIndexBinding.apk2.setOnFocusChangeListener(focusScaleListener);
        layoutIndexBinding.apk3.setOnFocusChangeListener(focusScaleListener);
        layoutIndexBinding.apk4.setOnFocusChangeListener(focusScaleListener);
    }



//    请求天气和温度
    private  void getGeoAndWeather(String locationString){
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
    }


//    从接口获取数据
    private void getDataFromHttp() throws JSONException {
        if (HOTEL_MESSAGE == 0){
            JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
            if (hotelMessageJson != null){
                strHotelName = hotelMessageJson.getString("name");
                strHotelLogo = hotelMessageJson.getString("iconUrl");;
                strHotelBg = hotelMessageJson.getString("homepageBackground");;
                strResourceUrl = hotelMessageJson.getString("resourceUrl");;
                strDetail = hotelMessageJson.getString("detail");;
                strVideoUrl = hotelMessageJson.getString("videoUrl");;
            }
            HOTEL_MESSAGE = 1;
        }
//        请求滚动栏
        if (TEXT == 0){
            BackstageHttp.getInstance().getTvText(serverAddress, tenant, new BackstageHttp.TvTextCallback() {
                @Override
                public void onTvTextResponse(String tvText, String tvTextColor,int code) {
                    strTvText = tvText;
                    strTvTextColor = tvTextColor;
                    TEXT = 1;
                }

                @Override
                public void onTvTextFailure(int code, String msg) {
                    strTvText = "";
                    strTvTextColor = "";
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
                    if (roomName.equals("")){
                        strRoomName = Const.MSG_ROOM_NOT_EXIST;
                        strWifiName = Const.MSG_ROOM_NOT_EXIST;
                    }else {
                        strRoomName = roomName;
                        strWifiName = roomNumber + "_" +roomName;
                    }

                    strWifiPassword = wifiPassword;
                    strDeskNumber = frontDeskPhone;
                    ROOM_MESSAGE =1;
                }
                @Override
                public void onRoomMessageFailure(int code, String msg) {
                    if(code == -1){
                        strRoomName = "";
                        strWifiName = roomNumber + "_" ;
                        strWifiPassword = "";
                        strDeskNumber = "";
                        ROOM_MESSAGE = 1 ;
                    }
                }
            });
        }

//        if (HOTEL_LIST == 0){
//            BackstageHttp.getInstance().getHotelList(serverAddress, tenant, new BackstageHttp.HotelListCallBack() {
//                @Override
//                public void onHotelListResponse(ArrayList<HotelListModel> hotelListModels) {
//                    try {
//                        hotelList.clear();
//                        hotelList.addAll(hotelListModels);
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                int cur_line;
//                                for (int i = 0; i < hotelList.size(); i++) {
//                                    cur_line = i/2;
//                                    Log.d(TAG, "onApkResponse: " + hotelList.get(i).getName());
//                                    //获取到每一个item的layout替换掉图片和文字和跳转地址
//                                    LinearLayout item = (LinearLayout) ((LinearLayout)layoutIndexBinding.apkLayout.getChildAt(cur_line)).getChildAt(i%2);
//                                    Glide.with(IndexActivity.this)
//                                            .load(hotelList.get(i).getPicUrl())
//                                            .error(R.color.white)
//                                            .into((ImageView) item.findViewById(R.id.apk_logo));
//                                    ((TextView)item.findViewById(R.id.apk_name)).setText(hotelList.get(i).getName());
//                                    int finalI  = i;
//                                    switch (hotelList.get(i).getType()){
//
//                                        case 10:
//                                            item.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    Intent intent = new Intent(IndexActivity.this, MainActivity.class);
//                                                    startActivity(intent);
//                                                }
//                                            });
//                                            break;
//                                        case 9:
//                                            item.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    Intent intent = new Intent(IndexActivity.this, AppActivity.class);
//                                                    intent.putExtra("bg",strHotelBg);
//                                                    intent.putExtra("serverAddress",serverAddress);
//                                                    intent.putExtra("tenant",tenant);
//                                                    intent.putExtra("title",hotelList.get(finalI).getName());
//                                                    intent.putExtra("type",1);
//                                                    startActivity(intent);
//                                                }
//                                            });
//                                            break;
//                                        case 0:
//
//                                            item.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    Intent intent = new Intent(IndexActivity.this,HotelActivity.class);
//                                                    intent.putExtra("bg",strHotelBg);
//                                                    intent.putExtra("serverAddress",serverAddress);
//                                                    intent.putExtra("tenant",tenant);
//                                                    intent.putExtra("title",hotelList.get(finalI).getName());
//                                                    intent.putExtra("id",hotelList.get(finalI).getId());
//
//                                                    Log.d(TAG, "detail: " +  hotelList.get(finalI).getId());
//                                                    intent.putExtra("detail",strDetail);
//                                                    startActivity(intent);
//                                                }
//                                            });
//                                            break;
//                                        case 2:
//                                            item.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    Intent intent = new Intent(IndexActivity.this,VideoActivity.class);
//                                                    intent.putExtra("bg",strHotelBg);
//                                                    intent.putExtra("serverAddress",serverAddress);
//                                                    intent.putExtra("tenant",tenant);
//                                                    intent.putExtra("title",hotelList.get(finalI).getName());
//                                                    Log.d(TAG, "strVideoUrl: " +strVideoUrl);
//                                                    intent.putExtra("videoUrl",strVideoUrl);
//                                                    startActivity(intent);
//                                                }
//                                            });
//                                            break;
//
//
//                                    }
//
//                                }
//                            }
//                        });
//                    }catch (Exception e){
//
//                    }
//
//                }
//
//                @Override
//                public void onHotelLIstFailure(int code, String msg) {
//
//                }
//            });
//        }



    }


//    输入框
    private void showInputDialog() {
        layoutIndexBinding.indexInput.setVisibility(View.VISIBLE);
//        layoutIndexBinding.tvImage.setEnabled(false);
        layoutIndexBinding.inputSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 在点击事件中获取按下的按钮的 keycode
                int keyCode; // 初始化为未知的 keycode
                KeyEvent keyEvent = (KeyEvent) view.getTag(); // 从 view 的 tag 中获取 KeyEvent 对象
                if (keyEvent != null) {
                    keyCode = keyEvent.getKeyCode(); // 获取按下的按钮的 keycode
                    Toast.makeText(IndexActivity.this, "!!!:" + keyCode, Toast.LENGTH_SHORT).show();
                }
                serverAddress = layoutIndexBinding.inputServerAddress.getText().toString();
                roomNumber = layoutIndexBinding.inputRoomNumber.getText().toString();
                tenant = layoutIndexBinding.inputTenant.getText().toString();
                // 保存服务器地址和房间号到 SharedPreferences
                saveServerAddressAndRoomNumber(serverAddress, roomNumber,tenant);
                initUI();
                layoutIndexBinding.indexInput.setVisibility(View.GONE);
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



}


