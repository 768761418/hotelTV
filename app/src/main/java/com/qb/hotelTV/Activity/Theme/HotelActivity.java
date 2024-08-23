package com.qb.hotelTV.Activity.Theme;
import static com.qb.hotelTV.Utils.TimeUtil.getCurrentDateTime;

import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.qb.hotelTV.Activity.HomeActivity;
import com.qb.hotelTV.Data.CommonData;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Model.HotelListModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Service.WebSocketService;
import com.qb.hotelTV.Setting.ApplicationSetting;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.databinding.LayoutIndexBinding;
import com.qb.hotelTV.module.InputMessageDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class HotelActivity extends ThemeActivity {
    LayoutIndexBinding layoutIndexBinding;
    private final String  TAG = HotelActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        初始化
        layoutIndexBinding = DataBindingUtil.setContentView(this, R.layout.layout_index);
        sharedPreferencesUtils = SharedPreferencesUtils.getInstance(this);
        inputMessageDialog = new InputMessageDialog(HotelActivity.this);
//        获取数据
        String[] data = commonData.getData();
        serverAddress = data[0];
        tenant =data[1];
        roomNumber = data[2];
        //        启动WebSocket配置的service
        websocketService = new Intent(this, WebSocketService.class);
        startService(websocketService);

        initUI();
        Log.d(TAG, "onCreate: " + serverAddress);
        Log.d(TAG, "onCreate: " + tenant);
        Log.d(TAG, "onCreate: " + roomNumber);
//        组件动画
        focusChange();
    }
    private void initUI(){
//        获取时间
        startUpdateTask(layoutIndexBinding.indexDate, layoutIndexBinding.indexTime);
//        获取地址和天气
        getGeoAndWeather(layoutIndexBinding.indexGeo,layoutIndexBinding.indexWeather);
        try {
            getDataFromHttp();
        }catch (Exception e){
            Log.e(TAG, "initUI: ", e);
        }
        //        启动定时更新公告任务
        startUpdateTvTextTask();
    }






//    焦点切换动画
    private void focusChange(){
        layoutIndexBinding.apk1.requestFocus();
        focusScaleListener.needBorder(true);
        layoutIndexBinding.apk1.setOnFocusChangeListener(focusScaleListener);
        layoutIndexBinding.apk2.setOnFocusChangeListener(focusScaleListener);
        layoutIndexBinding.apk3.setOnFocusChangeListener(focusScaleListener);
        layoutIndexBinding.apk4.setOnFocusChangeListener(focusScaleListener);
    }





//    从接口获取数据
    private void getDataFromHttp(){

//        //        获取配置信息
        initStartVideoOrImg(HotelActivity.this,
                serverAddress,
                tenant,
                layoutIndexBinding.indexLogo,
                layoutIndexBinding.indexBackground,
                layoutIndexBinding.indexTv

        );

        //      请求滚动栏公告
        getAnnouncements(HotelActivity.this,serverAddress, tenant,layoutIndexBinding.indexTvText);
//        获取界面列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HotelListModel> hotelListModels = BackstageHttp.getInstance().getHotelList(serverAddress, tenant,4);
                if (!hotelListModels.isEmpty()){
                    indexListOnclick(HotelActivity.this,layoutIndexBinding.apkLayout,hotelListModels,false,serverAddress,tenant,4);
                }
//                获取房间信息
                JSONObject roomData = BackstageHttp.getInstance().getRoomMessage(serverAddress, roomNumber, tenant);
                if (roomData != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                layoutIndexBinding.indexRoomNumber.setText(roomData.getString("roomNumber"));
                                layoutIndexBinding.indexWifiName.setText(roomData.getString("roomName"));
                                layoutIndexBinding.indexWifiPassword.setText(roomData.getString("wifiPassword"));
                            }catch (JSONException e){
                                Log.e(TAG, "解析信息错误：: ", e);

                            }

                        }
                    });
                }

            }
        }).start();
    }

//    显示dialog
    private void showInputDialog(boolean isFirst){
        if (!isFirst){
            inputMessageDialog.setMessage(serverAddress,roomNumber,tenant);
        }
        // 如果是第一次进入，则显示初始信息
        inputMessageDialog.show();
        inputMessageDialog.setSubmitCallback(new InputMessageDialog.SubmitCallback() {
            @Override
            public void onSubmitCallBack(String inputsServerAddress,String inputRoomNumber,String inputTenant) {
                serverAddress = inputsServerAddress;
                roomNumber = inputRoomNumber;
                tenant = inputTenant;
                initUI();
            }
        });

    }

    private void startUpdateTvTextTask(){
        Runnable startUpdateTvTextTask = new Runnable() {
            @Override
            public void run() {
                getAnnouncements(HotelActivity.this,serverAddress, tenant,layoutIndexBinding.indexTvText);
                boolean isLogin = CommonData.getInstance().getIsLogin();
                Log.d(TAG, "run: " + isLogin);
                if (!isLogin){
                    sharedPreferencesUtils.clearData();
                    showInputDialog(false);
                    Toast.makeText(HotelActivity.this,"该房间已被删除，请重新配置",Toast.LENGTH_SHORT).show();
                    indexListUnableOnclick(layoutIndexBinding.apkLayout,4);

                }else {
//                    initUI();
                    handler.postDelayed(this, 30*1000);
                }

            }
        };
        handler.post(startUpdateTvTextTask);
    }









}


