package com.qb.hotelTV.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dreamgyf.android.ui.widget.textview.marquee.MarqueeTextView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.qb.hotelTV.Activity.CommonActivity.AppActivity;
import com.qb.hotelTV.Activity.CommonActivity.ListActivity;
import com.qb.hotelTV.Activity.CommonActivity.VideoActivity;
import com.qb.hotelTV.Activity.CommonActivity.WebActivity;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Model.HotelListModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Setting.ApplicationSetting;
import com.qb.hotelTV.Utils.DownLoadUtil;
import com.qb.hotelTV.Utils.PermissionUtils;
import com.qb.hotelTV.Utils.PlayerUtils;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.huibuTv.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity {
    private Handler handler = new Handler();
    private String TAG = "HomeActivity";
    private Player player;
    private PlayerUtils playerUtils = new PlayerUtils();
    public SharedPreferencesUtils sharedPreferencesUtils;
    public DownLoadUtil downLoadUtil;




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

//    @Override
//    protected void onPause() {
//        super.onPause();
//        // 当 Activity 失去焦点时，暂停视频播放
//        if (player != null){
//            player.pause();
//        }
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        if (player != null){
//            // 当 Activity 重新获得焦点时，继续播放视频
//            player.play();
//        }
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
////        销毁时释放资源
//        if (player != null){
//            player = null;
//        }
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //请求权限
        PermissionUtils permissionUtils = new PermissionUtils();
        permissionUtils.checkPermission(this);
    }

    //    登录
    public boolean getLoginToken(Context context){
        sharedPreferencesUtils = SharedPreferencesUtils.getInstance(context);
//            拿取token
        String token = sharedPreferencesUtils.loadToken();
        if (token == null){
            return false;
        }
//            设置请求头
        String authorization = "Bearer " + token;
        BackstageHttp.getInstance().setToken(token);
        BackstageHttp.getInstance().setAuthorization(authorization);
        Log.d(TAG, "daying1: " + token);
        return true;
    }





    //    获取公告并修改组件
    public void getAnnouncements(Context context, String serverAddress, String tenant, MarqueeTextView view){
        BackstageHttp.getInstance().getTvText(serverAddress, tenant, new BackstageHttp.TvTextCallback() {
            @Override
            public void onTvTextResponse(String tvText, String tvTextColor,int code) {
                Log.d(TAG, "onTvTextResponse: " + tvText );
                Log.d(TAG, "onTvTextResponse: " +tvTextColor );
                if(code == 0){
                    //主线程修改公告
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (tvText == null || tvText.equals("")){
                                view.setVisibility(View.GONE);
                            }else {
                                view.setVisibility(View.VISIBLE);
                                view.setText(tvText);
                                if (tvTextColor!= null){
                                    int color = Color.parseColor(tvTextColor);
                                    view.setTextColor(color);
                                }
                            }
                        }
                    });
                } else if (code == 401) {
                    commonData.clearData();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //                    TODO 退出的时候暂停视频
                            if (player != null){
                                player.pause();
                                player = null;
                            }
                        }
                    });

                }
            }

            @Override
            public void onTvTextFailure(int code, String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                    }
                });

            }
        });
    }













}
