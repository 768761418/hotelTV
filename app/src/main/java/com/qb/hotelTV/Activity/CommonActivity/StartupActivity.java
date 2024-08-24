package com.qb.hotelTV.Activity.CommonActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.multidex.BuildConfig;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Activity.HomeActivity;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.databinding.LayoutStartupBinding;
import com.qb.hotelTV.module.InputMessageDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import com.azhon.appupdate.manager.DownloadManager;

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
                //将数据保存到内存共享，让其他Activity也可用
                commonData.setData(serverAddress,tenant,roomNumber);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkConfig();
                    }
                },2000);

            }
        });

    }

   /**
    判断主题和是否需要开机界面
    1.如果需要开机界面，就将主题传递给开机界面，让开机界面去判断
    2.如果不需要开机界面就判断需要打开哪个界面，然后把logo，视频,bg 传递过去
    **/
   private void checkConfig(){
       checkVersion();
       JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
       try{
           if (hotelMessageJson != null){
               JSONObject startData = hotelMessageJson.getJSONObject("startData");
               String themeType = hotelMessageJson.getString("themeType");
               int openTV = startData.getInt("openTV");
               if (openTV == 1){
                   int itemTV = startData.getInt("itemTV");
                   //判断是否需要开机动画
                   if (startData.getInt("open") == 1){
                       Intent intent = new Intent(StartupActivity.this  , StartVideoActivity.class);
                       intent.putExtra("startType",startData.getInt("type"));
                       intent.putExtra("startUrl",startData.getString("url"));
                       intent.putExtra("startSecond",startData.getLong("second"));
                       intent.putExtra("startIsOpenTxt",startData.getInt("openTxt"));
                       intent.putExtra("startContent",startData.getString("content"));
                       intent.putExtra("theme",themeType);
                       intent.putExtra("itemTV",itemTV);
                       startActivity(intent);
                       finish();
                   }else {
//               判断是否切换主题
                       if (checkTheme(StartupActivity.this,themeType,itemTV)){
                           finish();
                       }else {
                           Toast.makeText(StartupActivity.this,"请联系管理员配置正确的主题",Toast.LENGTH_SHORT).show();
                       }
                   }
               }else {
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
               }

           }else {
               showInputDialog(true);
               Toast.makeText(StartupActivity.this,hotelMessageJson.getString("msg"),Toast.LENGTH_SHORT).show();
           }
       }catch (JSONException e){
           showInputDialog(true);
           Toast.makeText(StartupActivity.this,"请联系管理员检查后台配置",Toast.LENGTH_SHORT).show();
       }catch (Exception e){
           showInputDialog(true);
           Toast.makeText(StartupActivity.this,"请联系管理员检查后台配置",Toast.LENGTH_SHORT).show();
       }

   }

   private void checkVersion(){
       Call call = BackstageHttp.getInstance().getAppVersion(serverAddress);
       call.enqueue(new Callback() {
           @Override
           public void onFailure(@NonNull Call call, @NonNull IOException e) {

           }

           @Override
           public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
               if (response.isSuccessful()){
                   String responseStr = response.body().string();
                   try {
                       JSONObject jsonObject = new JSONObject(responseStr);
                       //如果version code大於當前版本號，且version_name不一樣的時候，啟動更新
                       if(jsonObject.getInt("apk_code")>= BuildConfig.VERSION_CODE &&!jsonObject.getString("apk_name").equals(BuildConfig.VERSION_NAME)){
                           //获取本地版本号
                           DownloadManager manager = new DownloadManager.Builder(StartupActivity.this)
                                   .apkUrl(""+jsonObject.getString("apk_url"))
                                   .apkName("hyzj.apk")
                                   .smallIcon(R.mipmap.ic_launcher)
                                   //设置了此参数，那么内部会自动判断是否需要显示更新对话框，否则需要自己判断是否需要更新
                                   .apkVersionCode(jsonObject.getInt("apk_code"))
                                   //同时下面三个参数也必须要设置
                                   .apkVersionName("v"+jsonObject.getString("apk_name"))
                                   .apkDescription("更新描述信息")
                                   //省略一些非必须参数...
                                   .build();
                           manager.download();
                       }
                   } catch (JSONException e) {

                   }

           }
       }

       });
   }


}
