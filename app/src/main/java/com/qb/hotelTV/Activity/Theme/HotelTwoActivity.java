package com.qb.hotelTV.Activity.Theme;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.qb.hotelTV.Data.CommonData;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.HotelTwoFocusScaleListener;
import com.qb.hotelTV.Model.HotelListModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Service.WebSocketService;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.databinding.LayoutHotelTwoBinding;
import com.qb.hotelTV.module.InputMessageDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HotelTwoActivity extends ThemeActivity{

    private LayoutHotelTwoBinding layoutHotelTwoBinding;
    private String TAG = "HotelTwoActivity";

    private HotelTwoFocusScaleListener hotelTwoFocusScaleListener;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
//    初始化
    private void init(){
        layoutHotelTwoBinding = DataBindingUtil.setContentView(HotelTwoActivity.this, R.layout.layout_hotel_two);
        sharedPreferencesUtils = SharedPreferencesUtils.getInstance(this);
        inputMessageDialog = new InputMessageDialog(HotelTwoActivity.this);
        hotelTwoFocusScaleListener = new HotelTwoFocusScaleListener(this);
//        获取数据
        String[] data = commonData.getData();
        serverAddress = data[0];
        tenant =data[1];
        roomNumber = data[2];
        //        启动WebSocket配置的service
        websocketService = new Intent(this, WebSocketService.class);
        startService(websocketService);
        Log.d(TAG, "onCreate: " + serverAddress);
        Log.d(TAG, "onCreate: " + tenant);
        Log.d(TAG, "onCreate: " + roomNumber);
        initUI();
        focusChange();
    }


    private void initUI(){
        //        获取时间
        startUpdateTask(null, layoutHotelTwoBinding.time);
        //        获取地址和天气
        getGeoAndWeather(null,layoutHotelTwoBinding.weather);
        try {
            getDataFromHttp();
        }catch (Exception e){
            Log.e(TAG, "initUI: ", e);
        }

        //        启动定时更新公告任务
        startUpdateTvTextTask();
    }

    private void focusChange(){
        layoutHotelTwoBinding.item1.setOnFocusChangeListener(hotelTwoFocusScaleListener);
        layoutHotelTwoBinding.item2.setOnFocusChangeListener(hotelTwoFocusScaleListener);
        layoutHotelTwoBinding.item3.setOnFocusChangeListener(hotelTwoFocusScaleListener);
        layoutHotelTwoBinding.item4.setOnFocusChangeListener(hotelTwoFocusScaleListener);
        layoutHotelTwoBinding.item5.setOnFocusChangeListener(hotelTwoFocusScaleListener);
        layoutHotelTwoBinding.item6.setOnFocusChangeListener(hotelTwoFocusScaleListener);
        layoutHotelTwoBinding.item6.requestFocus();
    }

    private void getDataFromHttp(){
        //        //        获取配置信息
        initStartVideoOrImg(HotelTwoActivity.this,
                serverAddress,
                tenant,
                layoutHotelTwoBinding.hotelTwoLogo,
                layoutHotelTwoBinding.hotelTwoBackground,
                layoutHotelTwoBinding.hotelTwoTv
        );

        //      请求滚动栏公告
        getAnnouncements(HotelTwoActivity.this,serverAddress, tenant,layoutHotelTwoBinding.hotelTwoMarquee);

        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HotelListModel> hotelListModels = BackstageHttp.getInstance().getHotelList(serverAddress, tenant,6);
                if (!hotelListModels.isEmpty()){
                    indexListOnclick(HotelTwoActivity.this,layoutHotelTwoBinding.apkLayout,hotelListModels,false,serverAddress,tenant,4);
                }
//                获取房间信息
                JSONObject roomData = BackstageHttp.getInstance().getRoomMessage(serverAddress, roomNumber, tenant);
                if (roomData != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
//                                layoutIndexBinding.indexRoomNumber.setText(roomData.getString("roomNumber"));
                                layoutHotelTwoBinding.wifiName.setText(roomData.getString("roomName"));
                                layoutHotelTwoBinding.wifiPwd.setText(roomData.getString("wifiPassword"));
                                String front = roomData.getString("frontDeskPhone");
                                if (front != null && !front.equals("")){
                                    String frontDeskPhone = front.replace("\\n",System.lineSeparator());
                                    layoutHotelTwoBinding.frontDeskPhone.setText(frontDeskPhone);
                                }

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
                getAnnouncements(HotelTwoActivity.this,serverAddress, tenant,layoutHotelTwoBinding.hotelTwoMarquee);
                boolean isLogin = CommonData.getInstance().getIsLogin();
                Log.d(TAG, "run: " + isLogin);
                if (!isLogin){
                    sharedPreferencesUtils.clearData();
                    showInputDialog(false);
                    Toast.makeText(HotelTwoActivity.this,"该房间已被删除，请重新配置",Toast.LENGTH_SHORT).show();
                    indexListUnableOnclick(layoutHotelTwoBinding.apkLayout,4);

                }else {
//                    initUI();
                    handler.postDelayed(this, 30*1000);
                }

            }
        };
        handler.post(startUpdateTvTextTask);
    }


}
