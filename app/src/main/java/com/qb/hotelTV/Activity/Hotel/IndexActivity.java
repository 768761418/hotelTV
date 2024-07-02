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
import com.qb.hotelTV.Activity.Hospital.HospitalActivity;
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
import com.qb.hotelTV.Setting.ApplicationSetting;
import com.qb.hotelTV.Setting.ProgressDialogSetting;
import com.qb.hotelTV.Utils.PermissionUtils;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
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
//    从SharedPreferences中获取的数据
    private String serverAddress,tenant,roomNumber;
    private SharedPreferencesUtils sharedPreferencesUtils;


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
        super.onDestroy();
        setResult(ApplicationSetting.CLOSE_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        layoutIndexBinding = DataBindingUtil.setContentView(this, R.layout.layout_index);

        sharedPreferencesUtils = SharedPreferencesUtils.getInstance(this);
        boolean isFirstRun = sharedPreferencesUtils.loadIsFirstRun();
        if (isFirstRun) {
            // 如果是第一次进入，则显示输入对话框
            showInputDialog();
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
            initUI();
        }
//        显示房间号
        layoutIndexBinding.inputRoomNumber.setText(roomNumber);
//        组件动画
        focusChange();
    }
    private void initUI(){
//        获取时间
        startUpdateTask();
//        获取地址和天气
        getGeoAndWeather(layoutIndexBinding.indexGeo,layoutIndexBinding.indexWeather);
        try {
            getDataFromHttp();
        }catch (Exception e){
            Log.e(TAG, "initUI: ", e);
        }



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





//    从接口获取数据
    private void getDataFromHttp() throws JSONException {
        //        登录获取token
//        login(serverAddress,roomNumber,tenant);
        //        获取配置信息
        initStartVideoOrImg(IndexActivity.this,
                serverAddress,tenant,
                layoutIndexBinding.indexLogo,
                layoutIndexBinding.indexBackground,
                layoutIndexBinding.indexTv
        );
        //      请求滚动栏公告
        getAnnouncements(serverAddress, tenant,layoutIndexBinding.indexTvText);
//        获取界面列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HotelListModel> hotelListModels = BackstageHttp.getInstance().getHotelList(serverAddress, tenant,4);
                if (!hotelListModels.isEmpty()){
                    indexListOnclick(IndexActivity.this,layoutIndexBinding.apkLayout,hotelListModels,"");
                }
//                获取房间信息
                JSONObject roomData = BackstageHttp.getInstance().getRoomMessage(serverAddress, roomNumber, tenant);
                if (roomData != null){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try{
                                layoutIndexBinding.indexRoomName.setText(roomData.getString("roomName"));
                                layoutIndexBinding.indexWifiName.setText(roomData.getString("roomName") + "_" +roomNumber);
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
                //                将数据保存到内存共享，让其他Activity也可用
                commonData.setData(serverAddress,tenant,roomNumber);
                // 保存服务器地址和房间号到 SharedPreferences中
                sharedPreferencesUtils.saveInitData(serverAddress,roomNumber,tenant);
                initUI();
                layoutIndexBinding.indexInput.setVisibility(View.GONE);
//                layoutIndexBinding.tvImage.setEnabled(true);

            }
        });
    }





}


