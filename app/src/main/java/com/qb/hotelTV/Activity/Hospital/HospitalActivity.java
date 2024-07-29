package com.qb.hotelTV.Activity.Hospital;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.qb.hotelTV.Activity.AppActivity;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Activity.HomeActivity;
import com.qb.hotelTV.Activity.Hotel.IndexActivity;
import com.qb.hotelTV.Data.CommonData;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.WebSocketClient;
import com.qb.hotelTV.Model.HotelListModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Setting.ApplicationSetting;
import com.qb.hotelTV.Utils.PermissionUtils;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.databinding.LayoutHospitalBinding;
import com.qb.hotelTV.huibuTv.MainActivity;
import com.qb.hotelTV.module.InputMessageDialog;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HospitalActivity extends HomeActivity {
    private  String TAG = "HospitalActivity";
    LayoutHospitalBinding layoutHospitalBinding;
    private Handler handler = new Handler();
//    请求接口的请求头
    private String serverAddress,roomNumber,tenant;
//    websocket用的
    private WebSocketClient webSocketClient;
//    获取数据用的
    private SharedPreferencesUtils sharedPreferencesUtils;
//    输入配置的dialog
    private InputMessageDialog inputMessageDialog;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null){
            Log.d(TAG, "onDestroy: 234" );
            webSocketClient.close();
            webSocketClient = null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutHospitalBinding = DataBindingUtil.setContentView(this, R.layout.layout_hospital);
        sharedPreferencesUtils = SharedPreferencesUtils.getInstance(this);
        inputMessageDialog = new InputMessageDialog(HospitalActivity.this);

        boolean isFirstRun = sharedPreferencesUtils.loadIsFirstRun();
        if (isFirstRun) {
            showInputDialog(true);
        } else {
            // 如果不是第一次进入，则直接使用保存的服务器地址和房间号
            serverAddress = sharedPreferencesUtils.loadServerAddress();
            roomNumber = sharedPreferencesUtils.loadRoomNumber();
            tenant  = sharedPreferencesUtils.loadTenant();
            commonData.setData(serverAddress,tenant,roomNumber);
            Log.d(TAG, "serverAddress: " +serverAddress);
            Log.d(TAG, "roomNumber: " + roomNumber);
            Log.d(TAG, "tenant: " +tenant);
            // 使用服务器地址和房间号
            // ...
            boolean isGetToken = getLoginToken(HospitalActivity.this);
            if (isGetToken){
                initUI();
            }else {
                showInputDialog(true);
            }

        }
//        显示房间号
        layoutHospitalBinding.hospitalTop.setRoomNumber(roomNumber);

    }



    private void initUI(){
        layoutHospitalBinding.inputRoomNumber.setText(roomNumber);
//      获取天气和地址
        getGeoAndWeather(null,layoutHospitalBinding.hospitalTop.weather());

//        请求接口获取数据
        try {
            getDataFromHttp();
        }catch (Exception e){
            Log.e(TAG, "initUI: ", e);
        }
        String webSocketUrl = getWebSocketUrl(serverAddress);
        Log.d(TAG, "webSocketUrl: "+webSocketUrl);
        if (webSocketUrl != null){
            webSocketClient = initWebSocket(this,webSocketUrl);
        }
//        焦点切换动画
        focusChange();
//        启动定时更新公告任务
        startUpdateTvTextTask();
    }

//    焦点切换动画
    private void focusChange(){
        layoutHospitalBinding.hospitalModule1.requestFocus();
//        设置不需要边框
        focusScaleListener.needBorder(true);
        layoutHospitalBinding.hospitalModule1.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalBinding.hospitalModule2.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalBinding.hospitalModule3.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalBinding.hospitalModule4.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalBinding.hospitalModule5.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalBinding.hospitalModule6.setOnFocusChangeListener(focusScaleListener);

    }


    //    从接口获取数据
    private void getDataFromHttp()  {
//        登录获取token
        checkTheme();

//      请求滚动栏公告
        getAnnouncements(HospitalActivity.this,serverAddress, tenant,layoutHospitalBinding.hospitalTvText);
//        获取界面列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HotelListModel> hotelListModels = BackstageHttp.getInstance().getHotelList(serverAddress, tenant,6);
                if (!hotelListModels.isEmpty()){
                    indexListOnclick(HospitalActivity.this,layoutHospitalBinding.hospitalMainBottomLayout,hotelListModels,ApplicationSetting.THEME_HOSPITAL_ONE);
                }
            }
        }).start();
    }



//    定时更新公告栏数据
    private void startUpdateTvTextTask(){
        Runnable startUpdateTvTextTask = new Runnable() {
            @Override
            public void run() {
                getAnnouncements(HospitalActivity.this,serverAddress, tenant,layoutHospitalBinding.hospitalTvText);
                boolean isLogin = CommonData.getInstance().getIsLogin();
                Log.d(TAG, "run: " + isLogin);
                if (!isLogin){
                    showInputDialog(false);
                    Toast.makeText(HospitalActivity.this,"该房间已被删除，请重新配置",Toast.LENGTH_SHORT).show();
                    indexListUnableOnclick(layoutHospitalBinding.hospitalMainBottomLayout,6);
                    sharedPreferencesUtils.clearData();
                }else {
                    handler.postDelayed(this, 15*1000);
                }

            }
        };
        handler.post(startUpdateTvTextTask);
    }

    private void checkTheme(){
        JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
        String themeType;
        try{
            themeType = hotelMessageJson.getString("themeType");
            if (themeType.equals(ApplicationSetting.THEME_HOTEL_ONE)){
                Intent intent = new Intent(HospitalActivity.this, IndexActivity.class);
                startActivityForResult(intent,ApplicationSetting.CLOSE_CODE);
            }else {
                //        获取配置信息
                initStartVideoOrImg(HospitalActivity.this,
                        serverAddress,tenant,
                        layoutHospitalBinding.hospitalTop.logo(),
                        layoutHospitalBinding.hospitalBackground,
                        layoutHospitalBinding.hospitalTv
                );
            }
        }catch (JSONException e){
            Log.e(TAG, "checkTheme: ", e);
        }

    }

    private void showInputDialog(boolean isFirst){
        if (!isFirst){
            inputMessageDialog.setMessage(serverAddress,roomNumber,tenant);
        }
        inputMessageDialog.show();
        inputMessageDialog.setSubmitCallback(new InputMessageDialog.SubmitCallback() {
            @Override
            public void onSubmitCallBack(String inputsServerAddress,String inputRoomNumber,String inputTenant) {
                serverAddress = inputsServerAddress;
                roomNumber = inputRoomNumber;
                tenant = inputTenant;
                Log.d(TAG, "onSubmitCallBack: " + serverAddress);
                Log.d(TAG, "onSubmitCallBack: " + roomNumber);
                Log.d(TAG, "onSubmitCallBack: " + tenant);
                initUI();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        如果使用其他主体则在其他主体返回的时候关闭这个界面
        if (requestCode == ApplicationSetting.CLOSE_CODE ){
            finish();
        }
    }
}
