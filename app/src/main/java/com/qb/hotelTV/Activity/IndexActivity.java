package com.qb.hotelTV.Activity;
import static com.qb.hotelTV.Utils.TimeUtil.getCurrentDateTime;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.maning.updatelibrary.InstallUtils;
import com.qb.hotelTV.Adaptor.common.CommonAdapter;
import com.qb.hotelTV.Adaptor.common.CommonViewHolder;
import com.qb.hotelTV.Const;
import com.qb.hotelTV.Handler.CrashHandler;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Http.LocationHttp;
import com.qb.hotelTV.Setting.DownloadSetting;
import com.qb.hotelTV.Utils.PermissionUtils;
import com.qb.hotelTV.databinding.LayoutIndexBinding;
import com.qb.hotelTV.huibuTv.MainActivity;
import com.qb.hotelTV.huibuTv.PageAndListRowFragment;

import java.util.ArrayList;
import java.util.Date;
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
    private Integer GEO=0,WEATHER=0,TEXT=0,APK=0,ROOM_MESSAGE=0,HOTEL_MESSAGE=0,TV_CHANNEL =0;
//    视频是否全屏
    private boolean VIDEO_STATUS = false;
    private boolean FULL_SCREEN_SIDEBAR = false;

    private String geo,weather,strRoomName,
            strWifiName,strWifiPassword,strDeskNumber,
            strHotelName,strHotelLogo,strHotelBg,
            strTvText,strTvTextColor;

//    从SharedPreferences中获取的数据
    private String serverAddress,tenant,roomNumber;

//    用来存放apk的列表
    ArrayList<ApkModel> apkList = new ArrayList<>();
    ArrayList<VideoModel> videoList = new ArrayList<>();
    ProgressDialog progressDialog;
    private boolean currentKeyCodeIsEnter = false;

//    焦点选中动画
    FocusScaleListener focusScaleListener = new FocusScaleListener();

    private static final String KEY_SERVER_ADDRESS = "server_address";
    private static final String KEY_ROOM_NUMBER = "room_number";
    private static final String KEY_TENANT = "tenant";
    private static final String PREFS_NAME = "HotelTV";
    private boolean event_first = true;
    /* 上一次User有动作的Time Stamp */
    private Date lastUpdateTime;
    /* 计算User有几秒没有动作的 */
    private long timePeriod;
    /* 静止超过N秒将自动进入屏保 */
    private float mHoldStillTime = 10;
    /*标识当前是否进入了屏保*/
    private boolean isAuthorized = false;
    /*时间间隔*/
    private long intervalAuthorized = 100;
    private long intervalKeypadSaver = 1000;
    private Handler mHandler01 = new Handler();
    private Handler mHandler02 = new Handler();
    PageAndListRowFragment pageAndListRowFragment;

//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        int keyCode = event.getKeyCode();
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            if(FULL_SCREEN_SIDEBAR){
////                Toast.makeText(IndexActivity.this,"关闭侧边栏",Toast.LENGTH_SHORT).show();
//                setChannelLayoutParams(false);
//                FULL_SCREEN_SIDEBAR = false;
//                layoutIndexBinding.indexVideo.requestFocus();
//                return true;
//            }
//        }
////
//        View focusView = getCurrentFocus();
//        //TODO 这里每一个时间都会触发两次，先处理下
//        Log.d(TAG, "Key code: " + keyCode);
//        Log.d(TAG, "dispatchKeyEvent: " + currentKeyCodeIsEnter);
//        if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
//            if(focusView.getId() == R.id.index_video && event_first){
//                event_first = !event_first;
//                Log.d(TAG, "dispatchKeyEvent: index_video");
//                if(VIDEO_STATUS){
//                    Toast.makeText(this,"栏目出现",Toast.LENGTH_SHORT).show();
//                }else{
//                    setVideoMode();
//                }
//
//                return false;
//            }
////            Log.d(TAG, "dispatchKeyEvent1: " + currentKeyCodeIsEnter);
////            if (event.getAction() == KeyEvent.ACTION_DOWN) {
////                if (!currentKeyCodeIsEnter){
////                    Log.d(TAG, "dispatchKeyEvent2: " + currentKeyCodeIsEnter);
////                    currentKeyCodeIsEnter = true;
//////                    btnChangeVideoStatus();
////                }
////            }
////
////            return true;
//        }else if(keyCode == KeyEvent.KEYCODE_BACK){
//            if(VIDEO_STATUS&& focusView.getId()==R.id.index_video){
//                Toast.makeText(this,"关闭侧边栏，关闭全屏",Toast.LENGTH_SHORT).show();
//                //TODO 关闭侧边栏
//                //TODO 关闭全屏
//                setVideoMode();
//            }
//        }

//        return super.dispatchKeyEvent(event);
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState == null) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.main_browse_fragment, new PageAndListRowFragment(), "PageAndListRowFragment")
//                    .commitNow();
//        }
//        /* 初始取得User可触碰屏幕的时间 */
//        lastUpdateTime = new Date(System.currentTimeMillis());
//        mHandler02.postDelayed(mTask02, intervalAuthorized);
//        pageAndListRowFragment = (PageAndListRowFragment) getSupportFragmentManager().findFragmentByTag("PageAndListRowFragment");



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
            roomNumber = sharedPreferences.getString(KEY_ROOM_NUMBER, "");
            tenant  = sharedPreferences.getString(KEY_TENANT,"");
            // 使用服务器地址和房间号
            // ...
            initUI();
        }



//        btnChangeVideoStatus();
//        组件动画
        focusChange();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        openTv();
    }

    private void openTv(){
        layoutIndexBinding.tvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IndexActivity.this, MainActivity.class);
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
    protected void onDestroy() {
        // 移除所有未执行的任务，避免内存泄漏
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }


    private void setVideoMode(){
        ViewGroup.LayoutParams params = layoutIndexBinding.mainBrowseFragment.getLayoutParams();
//        ViewGroup.LayoutParams errMessage = layoutIndexBinding.indexVideoErr.getLayoutParams();
        if (VIDEO_STATUS){
                layoutIndexBinding.indexApkList.setVisibility(View.VISIBLE);
//                layoutIndexBinding.indexVideoChannelLayout.setVisibility(View.VISIBLE);
//                    将焦点恢复
//                layoutIndexBinding.indexVideo.setNextFocusRightId(R.id.index_apk_list);
                params.width = dpToPx(510); // 将 dp 转换为像素
                params.height = dpToPx(287); // 将 dp 转换为像素
//                errMessage.width = dpToPx(180);
//                layoutIndexBinding.indexVideoErr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
//            }
            VIDEO_STATUS = false;
        }else {
            layoutIndexBinding.indexApkList.setVisibility(View.GONE);
            layoutIndexBinding.indexVideoChannelLayout.setVisibility(View.GONE);
//                    设置焦点避免乱跑
            layoutIndexBinding.indexVideo.setNextFocusRightId(View.NO_ID);

            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
//            errMessage.width = dpToPx(800);
//            layoutIndexBinding.indexVideoErr.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
//                    params.width = 320;
//                    params.height = 240;
            VIDEO_STATUS = true;
        }
        // 应用修改后的参数到 View 上
        layoutIndexBinding.indexVideo.setLayoutParams(params);
//        layoutIndexBinding.indexVideoErr.setLayoutParams(errMessage);
    }

    private void btnChangeVideoStatus() {
        layoutIndexBinding.indexVideo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.d(TAG, "onKey: "+keyCode+"|"+event.getAction());
                if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    if(event.getAction()==KeyEvent.ACTION_DOWN){
                        if(VIDEO_STATUS){
                            FULL_SCREEN_SIDEBAR = true;
                            Toast.makeText(IndexActivity.this,"栏目出现",Toast.LENGTH_SHORT).show();
                            setChannelLayoutParams(true);
                            layoutIndexBinding.indexVideoChannel.requestFocus();
                        }else
                            setVideoMode();
                    }
                }else if(keyCode == KeyEvent.KEYCODE_BACK){
                    if(event.getAction()==KeyEvent.ACTION_DOWN){
                        if(FULL_SCREEN_SIDEBAR){
                            Toast.makeText(IndexActivity.this,"关闭侧边栏",Toast.LENGTH_SHORT).show();
                            setChannelLayoutParams(false);
                            FULL_SCREEN_SIDEBAR = false;
                            return true;
                        }
                        if(VIDEO_STATUS){
                            Toast.makeText(IndexActivity.this,"关闭全屏",Toast.LENGTH_SHORT).show();
                            //TODO 关闭侧边栏
                            //TODO 关闭全屏
                            setVideoMode();
                            return true;
                        }
                    }

                }
                return false;
            }
        });

        layoutIndexBinding.indexVideoChannel.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    if(FULL_SCREEN_SIDEBAR){
                        Toast.makeText(IndexActivity.this,"关闭侧边栏",Toast.LENGTH_SHORT).show();
                        setChannelLayoutParams(false);
                        FULL_SCREEN_SIDEBAR = false;
                        layoutIndexBinding.indexVideo.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

        layoutIndexBinding.indexVideoChannel.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    if(FULL_SCREEN_SIDEBAR){
                        Toast.makeText(IndexActivity.this,"关闭侧边栏",Toast.LENGTH_SHORT).show();
                        setChannelLayoutParams(false);
                        FULL_SCREEN_SIDEBAR = false;
                        layoutIndexBinding.indexVideo.requestFocus();
                        return true;
                    }
                }
                return false;
            }
        });

    }
    // 将 dp 单位转换为像素
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }


    private void setChannelLayoutParams(boolean isFirstScenario) {
        ViewGroup.LayoutParams params = layoutIndexBinding.indexVideoChannelLayout.getLayoutParams();
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) layoutIndexBinding.indexVideoChannelLayout.getLayoutParams();
        if (isFirstScenario) {
            layoutIndexBinding.indexVideoChannelLayout.setVisibility(View.VISIBLE);
            params.width = dpToPx(300);
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            // 设置左边对齐父布局
            layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
            // 清除其他约束
            layoutParams.leftToRight = ConstraintLayout.LayoutParams.UNSET;
            layoutParams.topToBottom = ConstraintLayout.LayoutParams.UNSET;
            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;
        } else {
            layoutIndexBinding.indexVideoChannelLayout.setVisibility(View.GONE);
            params.width =dpToPx(100);
            params.height=dpToPx(270);
            // 设置其他约束
            layoutParams.leftToRight = R.id.index_video;
            layoutParams.topToBottom = R.id.index_top;
            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            // 清除其他约束
            layoutParams.leftToLeft = ConstraintLayout.LayoutParams.UNSET;
        }

        layoutIndexBinding.indexVideoChannelLayout.setLayoutParams(layoutParams);
        layoutIndexBinding.indexVideoChannelLayout.setLayoutParams(params);

    }


    private void initUI(){
//        获取时间
        startUpdateTask();
        initAdapter();
//        获取经纬度
        getLocation();

//        请求多个接口获取数据
        getGeoAndWeather(locationString);
        getDataFromHttp();

        showProgressDialog();
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if(GEO == 1 && WEATHER == 1 &&ROOM_MESSAGE ==1 && TEXT==1 && TV_CHANNEL == 1 ){
                    dismissProgressDialog();
                    timer.cancel();
//                    在主线程修改组件
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (strHotelName == null || strHotelName.equals("")){
                                layoutIndexBinding.indexName.setText(Const.MSG_NETWORK_ERR);
                            }else {
                                layoutIndexBinding.indexName.setText(strHotelName);
                            }

                            if (strHotelLogo != null){
                                Glide.with(IndexActivity.this)
                                        .load(strHotelLogo)
                                        .error(R.drawable.img)
                                        .into(layoutIndexBinding.indexLogo);
                            }
                            if (strHotelBg != null){
                                Glide.with(IndexActivity.this)
                                        .load(strHotelBg)
                                        .error(R.drawable.app_bg)
                                        .into(layoutIndexBinding.indexBackground);

                            }


                            layoutIndexBinding.indexSky.setText(geo + "  " + weather);

                            layoutIndexBinding.indexRoomName.setText(strRoomName);

                            layoutIndexBinding.indexWifiName.setText(strWifiName);
                            layoutIndexBinding.indexWifiPassword.setText(strWifiPassword);
                            layoutIndexBinding.indexDeskNumber.setText(strDeskNumber);
//                            apk的列表
//                            layoutIndexBinding.indexApk.setAdapter(apkAdaptor);
                            layoutIndexBinding.indexVideoChannel.setAdapter(videoModelAdapter);
                            if (strTvText == null || strTvText.equals("")){
                                Toast.makeText(IndexActivity.this, Const.MSG_NETWORK_ERR, Toast.LENGTH_SHORT).show();
                            }else {
                                layoutIndexBinding.indexTvText.setText(strTvText);
                            }
                        }
                    });
                }
            }
        };
        timer.schedule(timerTask,0,1000);
        layoutIndexBinding.indexVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                hideVideoLoading();
                Drawable drawable = ContextCompat.getDrawable(IndexActivity.this, R.drawable.tv_err);
                layoutIndexBinding.indexVideo.setBackground(drawable);
                layoutIndexBinding.indexVideoErr.setVisibility(View.VISIBLE);
                Toast.makeText(IndexActivity.this, Const.MSG_TV_ERR, Toast.LENGTH_SHORT).show();
                return true; // 返回 true 表示已经处理了错误
            }
        });

        // 设置视频加载监听器
        layoutIndexBinding.indexVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // 视频已准备好，移除加载等待
                hideVideoLoading();
            }
        });

        layoutIndexBinding.indexVideo.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
                // 视频已准备好，移除加载等待
                hideVideoLoading();
                return true;
            }
        });
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

        videoModelAdapter = new CommonAdapter<VideoModel>(this,videoList,R.layout.item_tv) {
            @Override
            public void bindData(CommonViewHolder holder, VideoModel data, int position) {
                holder.setText(R.id.tv_name,data.getStreamName());
                holder.itemView.setOnFocusChangeListener(focusScaleListener);

                holder.setCommonClickListener(new CommonViewHolder.OnCommonItemEventListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        //TODO
                        String url = data.getStreamUrl();
                        if (current_channel != data.getId()){
                            current_channel = data.getId();
                            Log.d(TAG, "viedo" + url);
                            layoutIndexBinding.indexVideo.setBackgroundResource(android.R.color.transparent);
                            layoutIndexBinding.indexVideoErr.setVisibility(View.GONE);
                            showVideoLoading();
                            // 停止当前视频播放并释放资源
                            layoutIndexBinding.indexVideo.stopPlayback();
                            layoutIndexBinding.indexVideo.setVideoURI(Uri.parse(url));
                            layoutIndexBinding.indexVideo.start();
                        }
                    }

                    @Override
                    public void onItemLongClick(int viewId, int position) {

                    }
                });
            }
        };
    }


    private void showVideoLoading(){
        layoutIndexBinding.indexVideoLoading.setVisibility(View.VISIBLE);
        layoutIndexBinding.indexVideo.setEnabled(false);

    }
    private void hideVideoLoading(){
        layoutIndexBinding.indexVideoLoading.setVisibility(View.GONE);
        layoutIndexBinding.indexVideo.setEnabled(true);
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

//    外部启动apk
    private void gotoOtherApp(String packageName,String apkUrl){
        Log.d(TAG, "gotoOtherApp: "+packageName);
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            // 应用未安装或包名无效

            if(apkUrl.equals("")){
                Toast.makeText(this, Const.MSG_APK_NOT_EXIST + ",请联系管理员添加apk文件", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, Const.MSG_APK_NOT_EXIST, Toast.LENGTH_SHORT).show();
                installApp( apkUrl);
            }

        }
    }

//    焦点切换动画
    private void focusChange(){
//        layoutIndexBinding.indexVideo.requestFocus();
        layoutIndexBinding.tvImage.requestFocus();
        layoutIndexBinding.tvImage.setOnFocusChangeListener(focusScaleListener);
//        layoutIndexBinding.mainBrowseFragment.requestFocus();
//        layoutIndexBinding.mainBrowseFragment.setOnFocusChangeListener(focusScaleListener);
        layoutIndexBinding.indexVideo.setOnFocusChangeListener(focusScaleListener);
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
    private void getDataFromHttp(){
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
                    strHotelName = "";

                    strHotelBg = "";
                    HOTEL_MESSAGE = 1;
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

//        请求apk列表
        if (APK == 0){
            BackstageHttp.getInstance().getApk(serverAddress, tenant,new BackstageHttp.ApkCallback() {
                @Override
                public void onApkResponse(ArrayList<ApkModel> apkModelArrayList) {
                    try {
                        apkList.clear();
                        apkList.addAll(apkModelArrayList);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int cur_line = 0;
                                for (int i = 0; i < apkList.size(); i++) {
                                    cur_line = i/2;
                                    Log.d(TAG, "onApkResponse: " + apkList.get(i).getName());
                                    //获取到每一个item的layout替换掉图片和文字和跳转地址
                                    LinearLayout item = (LinearLayout) ((LinearLayout)layoutIndexBinding.apkLayout.getChildAt(cur_line)).getChildAt(i%2);
                                    Glide.with(IndexActivity.this)
                                            .load(apkList.get(i).getLogoUrl())
                                            .error(R.color.white)
                                            .into((ImageView) item.findViewById(R.id.apk_logo));
                                    ((TextView)item.findViewById(R.id.apk_name)).setText(apkList.get(i).getName());
                                    int finalI = i;
                                    item.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (apkList.get(finalI).getApkUrl() == null || apkList.get(finalI).getApkUrl().equals("")){
                                                gotoOtherApp(apkList.get(finalI).getSchemeUrl(),"");
                                            }else {
                                                gotoOtherApp(apkList.get(finalI).getSchemeUrl(),apkList.get(finalI).getApkUrl());
                                            }

                                        }
                                    });
                                }
                            }
                        });



//                        apkAdaptor.notifyDataSetChanged();
                    }catch (Exception e){
                        Log.e(TAG, "数据写入出错了 ",e);
                    }

                    APK = 1;
                }

                @Override
                public void onApkFailure(int code, String msg) {
                    Log.d(TAG, "onRoomMessageFailure: ");
                    APK = 1;
                }
            });
        }

        if (TV_CHANNEL == 0){
            BackstageHttp.getInstance().getTvChannel(serverAddress, tenant, new BackstageHttp.TvChannelCallback() {
                @Override
                public void onTvChannelResponse(ArrayList<VideoModel> videoModels) {
                    try {
                        videoList.addAll(videoModels);
                        videoModelAdapter.notifyDataSetChanged();
                        String url = videoList.get(0).getStreamUrl();
                        current_channel = videoList.get(0).getId();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                layoutIndexBinding.indexVideo.setBackgroundResource(android.R.color.transparent);
                                layoutIndexBinding.indexVideoErr.setVisibility(View.GONE);
                                layoutIndexBinding.indexVideo.setVideoURI(Uri.parse(url));
                                layoutIndexBinding.indexVideo.start();
                            }
                        });
                        TV_CHANNEL =1 ;
                    }catch (Exception e){
                        Log.e(TAG, "数据写入出错了 ", e);
                        TV_CHANNEL =1 ;
                    }
                }

                @Override
                public void onTvChannelFailure(int code, String msg) {
                    TV_CHANNEL =1 ;
                }
            });
        }
        Log.d(TAG, "finish");


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
        layoutIndexBinding.indexInput.setVisibility(View.VISIBLE);
        layoutIndexBinding.indexVideo.setEnabled(false);
        layoutIndexBinding.inputSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 在点击事件中获取按下的按钮的 keycode
                int keyCode = KeyEvent.KEYCODE_UNKNOWN; // 初始化为未知的 keycode
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
                layoutIndexBinding.indexVideo.setEnabled(true);
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


    private void installApp(String apkUrl){
        InstallUtils.checkInstallPermission(IndexActivity.this, new InstallUtils.InstallPermissionCallBack() {
            @Override
            public void onGranted() {
                downloadApp(apkUrl);
            }

            @Override
            public void onDenied() {
                //弹出弹框提醒用户
                AlertDialog alertDialog = new AlertDialog.Builder(IndexActivity.this)
                        .setTitle("温馨提示")
                        .setMessage("必须授权才能安装APK，请设置允许安装")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //打开设置页面
                                InstallUtils.openInstallPermissionSetting(IndexActivity.this, new InstallUtils.InstallPermissionCallBack() {
                                    @Override
                                    public void onGranted() {
                                        //去下载Apk
                                        downloadApp(apkUrl);
                                    }

                                    @Override
                                    public void onDenied() {
                                        //还是不允许咋搞？
                                        Toast.makeText(IndexActivity.this, "必须授权才能安装APK，请设置允许安装！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }

    private void downloadApp(String apkUrl){
        showProgressDialog();
//2.下载APK
        InstallUtils.with(this)
                //必须-下载地址
                .setApkUrl(apkUrl)
                //非必须-下载保存的文件的完整路径+/name.apk，使用自定义路径需要获取读写权限
                .setApkPath(DownloadSetting.APK_PATH + "xxx.apk")
                //非必须-下载回调
                .setCallBack(new InstallUtils.DownloadCallBack() {
                    @Override
                    public void onStart() {
                        //下载开始
                    }

                    @Override
                    public void onComplete(String path) {
                        dismissProgressDialog();
                        //下载完成
                        //安装APK
                        /**
                         * 安装APK工具类
                         * @param activity       上下文
                         * @param filePath      文件路径
                         * @param callBack      安装界面成功调起的回调
                         */
                        InstallUtils.installAPK(IndexActivity.this, path, new InstallUtils.InstallCallBack() {
                            @Override
                            public void onSuccess() {
                                //onSuccess：表示系统的安装界面被打开
                                //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
                                Toast.makeText(IndexActivity.this, "正在安装程序", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail(Exception e) {
                                Toast.makeText(IndexActivity.this, "安装失败:" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onLoading(long total, long current) {
                        //下载中
                    }

                    @Override
                    public void onFail(Exception e) {
                        //下载失败
                    }

                    @Override
                    public void cancle() {
                        //下载取消
                    }
                })
                //开始下载
                .startDownload();
    }


    /**
     * 计时线程
//     */
//    private Runnable mTask01 = new Runnable() {
//
//        @Override
//        public void run() {
//            Date timeNow = new Date(System.currentTimeMillis());
//            /* 计算User静止不动作的时间间距 */
//            /**当前的系统时间 - 上次触摸屏幕的时间 = 静止不动的时间**/
//            timePeriod = (long) timeNow.getTime() - (long) lastUpdateTime.getTime();
//            /*将静止时间毫秒换算成秒*/
//            float timePeriodSecond = ((float) timePeriod / 1000);
//
////            Log.d(TAG, "timePeriodSecond: " + timePeriodSecond);
////            Log.d(TAG, "isShowingHeaders: " + pageAndListRowFragment.isShowingHeaders());
//            if(pageAndListRowFragment.isShowingHeaders()){
//                if(timePeriodSecond > mHoldStillTime){
//                Toast.makeText(IndexActivity.this, "10s未操作", Toast.LENGTH_SHORT).show();
////                模拟点击当前焦点的位置
//                    pageAndListRowFragment.getView().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_CENTER));
//                    pageAndListRowFragment.getView().dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DPAD_CENTER));
////                打印pageAndListRowFragment.isShowingHeaders();
//                    updateUserActionTime();
//                }
//            }else{
//                updateUserActionTime();
//            }
//
//            /*反复调用自己进行检查*/
//            mHandler01.postDelayed(mTask01, intervalKeypadSaver);
//        }
//    };
//    /**
//     * 持续屏保显示线程
//     */
//    private Runnable mTask02 = new Runnable() {
//
//        @Override
//        public void run() {
////
//            if(isAuthorized){
//                //        开始计时
//                updateUserActionTime();
//                mHandler01.postDelayed(mTask01, intervalKeypadSaver);
//            }
//            else {
////                获取pageAndListRowFragment实例
//                PageAndListRowFragment pageAndListRowFragment = (PageAndListRowFragment) getSupportFragmentManager().findFragmentByTag("PageAndListRowFragment");
//                if(pageAndListRowFragment != null){
//                    isAuthorized = pageAndListRowFragment.isAuthorized();
//                }
//                /*反复调用自己进行检查*/
//                mHandler02.postDelayed(mTask02, intervalAuthorized);
//            }
//        }
//    };
//
//
//    /*用户有操作的时候不断重置静止时间和上次操作的时间*/
//    public void updateUserActionTime() {
//        Date timeNow = new Date(System.currentTimeMillis());
//        lastUpdateTime.setTime(timeNow.getTime());
//    }


////    获取activity返回的结果
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode == -1){
////            Toast.makeText(this, "返回结果", Toast.LENGTH_SHORT).show();
////            获取pageAndListRowFragment实例
//            PageAndListRowFragment pageAndListRowFragment = (PageAndListRowFragment) getSupportFragmentManager().findFragmentByTag("PageAndListRowFragment");
//            if(pageAndListRowFragment != null){
//                pageAndListRowFragment.setSelectedPosition(MyApplication.getChannelIndex(), false);
//                pageAndListRowFragment.showHeader();
//            }
//        }
//    }


//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (layoutIndexBinding.mainBrowseFragment.hasFocus()){
//            switch (keyCode) {
//                case KeyEvent.KEYCODE_DPAD_UP:
//                    pageAndListRowFragment.toggleChannel(false);
//                    layoutIndexBinding.mainBrowseFragment.requestFocus();
//                    Log.d(TAG, "onKeyDown: 上");
//                    return true;
//                case KeyEvent.KEYCODE_DPAD_DOWN:
//                    pageAndListRowFragment.toggleChannel(true);
//                    layoutIndexBinding.mainBrowseFragment.requestFocus();
//                    Log.d(TAG, "onKeyDown: 下");
//                    return  true;
//                case KeyEvent.KEYCODE_DPAD_RIGHT:
//                    changeFocus();
//                    Log.d(TAG, "onKeyDown: you");
//                    return true;
//                case KeyEvent.KEYCODE_DPAD_LEFT:
////                pageAndListRowFragment.toggleHeader();
//                    Log.d(TAG, "onKeyDown: 左");
//                    return true;
////                    return true;
////                case KeyEvent.KEYCODE_ENTER:
////                    setVideoMode();
////                    return true;
//
//        }
//
//
////
//
//
//////            case KeyEvent.KEYCODE_DPAD_CENTER:
////////                pageAndListRowFragment.toggleHeader();
//////                Log.d(TAG, "onKeyDown: 确定");
//////                break;
//////            case KeyEvent.KEYCODE_BACK:
//////                Log.d(TAG, "onKeyDown: 返回");
//////                break;
//////            case KeyEvent.KEYCODE_MENU:
//////                Log.d(TAG, "onKeyDown: 菜单");
//////                break;
//        }
////        return true;
//        return super.onKeyDown(keyCode, event);
//    }


    private void changeFocus(){
        layoutIndexBinding.apk1.requestFocus();
    }

//    @SuppressLint("RestrictedApi")
//    @Override
//    public boolean dispatchKeyEvent(KeyEvent event) {
//        updateUserActionTime();
//        return super.dispatchKeyEvent(event);
//    }

//    @Override
//    protected void onResume() {
//        updateUserActionTime();
//        if(isAuthorized){
//            /*activity显示的时候启动线程*/
//            mHandler01.postAtTime(mTask01, intervalKeypadSaver);
//        }else
//            mHandler02.postAtTime(mTask02, intervalAuthorized);
//        super.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        /*activity不可见的时候取消线程*/
//        mHandler01.removeCallbacks(mTask01);
//        mHandler02.removeCallbacks(mTask02);
//        super.onPause();
//    }


}


