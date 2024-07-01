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
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.WebSocketClient;
import com.qb.hotelTV.Model.HotelListModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.PermissionUtils;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.databinding.LayoutHospitalBinding;
import com.qb.hotelTV.huibuTv.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HospitalActivity extends BaseActivity {
    private  String TAG = "HospitalActivity";
    LayoutHospitalBinding layoutHospitalBinding;
    private Handler handler = new Handler();
//    请求接口的请求头
    private String serverAddress,roomNumber,tenant;
    private WebSocketClient webSocketClient;
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
        if (webSocketClient != null){
            webSocketClient.close();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutHospitalBinding = DataBindingUtil.setContentView(this, R.layout.layout_hospital);
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
            layoutHospitalBinding.hospitalTop.setRoomNumber(roomNumber);
            // 使用服务器地址和房间号
            // ...
            initUI();
        }

    }



    private void initUI(){
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
            initWebSocket(this,webSocketClient,webSocketUrl);
        }
//        焦点切换动画
        focusChange();
//        启动定时更新公告任务
        startUpdateTvTextTask();
    }


    private void showInputDialog() {
        layoutHospitalBinding.indexInput.setVisibility(View.VISIBLE);
//        layoutIndexBinding.tvImage.setEnabled(false);
        layoutHospitalBinding.inputSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serverAddress = layoutHospitalBinding.inputServerAddress.getText().toString();
                roomNumber = layoutHospitalBinding.inputRoomNumber.getText().toString();
                tenant = layoutHospitalBinding.inputTenant.getText().toString();
                commonData.setData(serverAddress,tenant,roomNumber);
                // 保存服务器地址和房间号到 SharedPreferences中
                sharedPreferencesUtils.saveInitData(serverAddress,roomNumber,tenant);
                initUI();
                layoutHospitalBinding.indexInput.setVisibility(View.GONE);
            }
        });
    }


//    焦点切换动画
    private void focusChange(){
        layoutHospitalBinding.hospitalModule0.requestFocus();
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
        login(serverAddress,roomNumber,tenant);
//        获取配置信息
        initStartVideoOrImg();
//      请求滚动栏公告
        getAnnouncements(serverAddress, tenant,layoutHospitalBinding.hospitalTvText);
//        获取界面列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<HotelListModel> hotelListModels = BackstageHttp.getInstance().getHotelList(serverAddress, tenant,6);
                if (!hotelListModels.isEmpty()){
                    indexListOnclick(HospitalActivity.this,layoutHospitalBinding.hospitalMainBottomLayout,hotelListModels);
                }
            }
        }).start();
    }



//    定时更新公告栏数据
    private void startUpdateTvTextTask(){
        Runnable startUpdateTvTextTask = new Runnable() {
            @Override
            public void run() {
                getAnnouncements(serverAddress, tenant,layoutHospitalBinding.hospitalTvText);
                handler.postDelayed(this, 5*1000);
            }
        };
        handler.post(startUpdateTvTextTask);
    }





//    初始化启动图片或视频
    private void initStartVideoOrImg(){
        try{
            JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
            String themeType = "hospital1";
            if (hotelMessageJson != null){
                String logoUrl = hotelMessageJson.getString("iconUrl");;
                String bgUrl = hotelMessageJson.getString("homepageBackground");
                String videoUrl = hotelMessageJson.getString("resourceUrl");
//                初始化背景和logo
                initLogoAndBackGround(HospitalActivity.this,layoutHospitalBinding.hospitalTop.logo(),logoUrl,layoutHospitalBinding.hospitalBackground,bgUrl);
                initIndexVideo(HospitalActivity.this,layoutHospitalBinding.hospitalTv,videoUrl);

                themeType = hotelMessageJson.getString("themeType");
            }
            JSONObject startData = hotelMessageJson.getJSONObject("startData");
//           判断是否需要开机动画
            if (startData.getInt("open") == 1){
                Intent intent = new Intent(HospitalActivity.this, HospitalStartVideoActivity.class);
                intent.putExtra("startType",startData.getInt("type"));
                intent.putExtra("startUrl",startData.getString("url"));
                intent.putExtra("startSecond",startData.getLong("second"));
                intent.putExtra("startIsOpenTxt",startData.getInt("openTxt"));
                intent.putExtra("startContent",startData.getString("content"));
                startActivity(intent);
            }
            // TODO 切换主题
            if (themeType.equals("hospital1")){

            }


        }catch (JSONException e){
            Log.e(TAG, "initStartVideoOrImg: ", e);
        }
    }




}
