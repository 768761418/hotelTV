package com.qb.hotelTV.Activity.CommonActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Activity.HomeActivity;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.databinding.LayoutStartupBinding;
import com.qb.hotelTV.module.InputMessageDialog;

import org.json.JSONException;
import org.json.JSONObject;


/***
    开机启动页
    请求配置接口，
    确定是不是请求的通，
    如果不通就弹出选择框，
    如果通过就看需不需要开机动画
 ***/
public class StartupActivity extends HomeActivity {
    private LayoutStartupBinding layoutStartupBinding;
    private SharedPreferencesUtils sharedPreferencesUtils;
    private InputMessageDialog inputMessageDialog;
    private String serverAddress,roomNumber,tenant;
    private String TAG = "StartupActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }


    private void initUI(){
        layoutStartupBinding = DataBindingUtil.setContentView(this, R.layout.layout_startup);
//        加载loading的gif
        Glide.with(this).
                load(R.drawable.loading).
                into(layoutStartupBinding.loadingIcon);
//        初始化
        sharedPreferencesUtils = SharedPreferencesUtils.getInstance(this);
        inputMessageDialog = new InputMessageDialog(StartupActivity.this);
//        判断是否登录
        checkLogin();
    }


    private void checkLogin(){
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
            boolean isGetToken = getLoginToken(StartupActivity.this);
            if (isGetToken){
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 延迟3秒后执行
                        checkConfig();
                    }
                }, 3000); // 延迟时间，单位毫秒
            }else {
                showInputDialog(true);
            }

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
                checkConfig();
            }
        });

    }

   /**
    判断主题和是否需要开机界面
    1.如果需要开机界面，就将主题传递给开机界面，让开机界面去判断
    2.如果不需要开机界面就判断需要打开哪个界面，然后把logo，视频,bg 传递过去
    **/
   private void checkConfig(){
       JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);

       try{
           if (hotelMessageJson != null){
               JSONObject startData = hotelMessageJson.getJSONObject("startData");
               String themeType = hotelMessageJson.getString("themeType");
                //判断是否需要开机动画
               if (startData.getInt("open") == 1){
                   Intent intent = new Intent(StartupActivity.this  , StartVideoActivity.class);
                   intent.putExtra("startType",startData.getInt("type"));
                   intent.putExtra("startUrl",startData.getString("url"));
                   intent.putExtra("startSecond",startData.getLong("second"));
                   intent.putExtra("startIsOpenTxt",startData.getInt("openTxt"));
                   intent.putExtra("startContent",startData.getString("content"));
                   intent.putExtra("theme",themeType);
                   startActivity(intent);
                   finish();
               }else {
//               判断是否切换主题
                   if (checkTheme(StartupActivity.this,themeType)){
                       finish();
                   }else {
                       Toast.makeText(StartupActivity.this,"请联系管理员配置正确的主题",Toast.LENGTH_SHORT).show();
                   }
               }
           }else {
               showInputDialog(true);
               Toast.makeText(StartupActivity.this,hotelMessageJson.getString("msg"),Toast.LENGTH_SHORT).show();
           }






       }catch (JSONException e){
           Log.e(TAG, "checkTheme: ", e);
       }catch (NullPointerException e){
           Log.e(TAG, "checkTheme: ", e);
       }

   }
}
