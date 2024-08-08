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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dreamgyf.android.ui.widget.textview.marquee.MarqueeTextView;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Activity.Hospital.HospitalListActivity;
import com.qb.hotelTV.Activity.Hospital.HospitalStartVideoActivity;
import com.qb.hotelTV.Activity.Hospital.HospitalVideoActivity;
import com.qb.hotelTV.Activity.Hospital.HospitalWebActivity;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.WebSocketClient;
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
import java.util.concurrent.CountDownLatch;

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
        super.onDestroy();
//        销毁时释放资源
        if (player != null){
            player = null;
        }
    }

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

    //    默认携带参数
    private void defaultPutIntent(Intent intent,String title,Long id){
        intent.putExtra("title",title);
        intent.putExtra("id",id);
    }
    //    界面点击模块事件
    public  <T extends ViewGroup> void indexListOnclick(Context context, T layout, ArrayList<HotelListModel> hotelListModels, String theme,String serverAddress,String tenant){
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < hotelListModels.size(); i++) {
                        Log.d(TAG, i + "onApkResponse: " + hotelListModels.get(i).getName());
                        Log.d(TAG, i +"onApkResponse: " + hotelListModels.get(i).getBackgroundUrl());
                        Log.d(TAG, i +"onApkResponse: " + hotelListModels.get(i).getPicUrl());
                        Log.d(TAG, i +"onApkResponse: " + hotelListModels.get(i).getType());



                        //获取到每一个item的layout替换掉图片和文字和跳转地址
                        LinearLayout item = (LinearLayout)  layout.getChildAt(i);

                        ImageView img  = item.findViewById(R.id.item_img);

                        Glide.with((Activity) context)
                                .load(hotelListModels.get(i).getPicUrl())
                                .error(R.color.black)
                                .into(img);

                        ((TextView)item.findViewById(R.id.item_text)).setText(hotelListModels.get(i).getName());
                        item.setTouchscreenBlocksFocus(true);
                        item.setFocusable(true);

                        if (theme.equals(ApplicationSetting.THEME_HOSPITAL_ONE)){
                            SimpleTarget<Drawable> simpleTarget = new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    item.setBackground(resource);
                                }
                            };
                            Glide.with((Activity) context)
                                    .load(hotelListModels.get(i).getBackgroundUrl())
                                    .error(R.color.white)
                                    .into(simpleTarget);
                        }

                        int finalI  = i;
                        switch (hotelListModels.get(i).getType()){
                            case 10:
//                                            电视界面
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case 9:
//                              应用中心
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, AppActivity.class);
                                        intent.putExtra("title",hotelListModels.get(finalI).getName());
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case 0:
//                              WEB图文
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, HospitalWebActivity.class);
                                        String title = hotelListModels.get(finalI).getName();
                                        Long id = hotelListModels.get(finalI).getId();
                                        defaultPutIntent(intent,title,id);
                                        Log.d(TAG, "onClick: " + title);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case 1:
                                // 图文列表
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, HospitalListActivity.class);
                                        String title = hotelListModels.get(finalI).getName();
                                        Long id = hotelListModels.get(finalI).getId();
                                        defaultPutIntent(intent,title,id);
                                        Log.d(TAG, "onClick: " + title);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case 2:
//                              视频
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, HospitalVideoActivity.class);
                                        String title = hotelListModels.get(finalI).getName();
                                        Long id = hotelListModels.get(finalI).getId();
                                        defaultPutIntent(intent,title,id);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case 4:
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String[] result = BackstageHttp.getInstance().getAppMessage(serverAddress,tenant,hotelListModels.get(finalI).getId());
                                                    Log.d(TAG, "lanmu: " + result[0]);
                                                    Log.d(TAG, "lanmu: " + result[1]);
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            if (result[1] == null || result[1].equals("")){
                                                                downLoadUtil.gotoOtherApp(result[0],"");
                                                            }else {
                                                                downLoadUtil.gotoOtherApp(result[0],result[1]);
                                                            }
                                                        }
                                                    });


                                                }
                                            }).start();



                                    }
                                });
                                break;

                        }

                    }
                }
            });
        }catch (Exception e){

        }

    }

    public <T extends ViewGroup> void indexListUnableOnclick(T layout,int listNumber){
        for(int i= 0;i<listNumber;i++){
            //获取到每一个item的layout替换掉图片和文字和跳转地址
            LinearLayout item = (LinearLayout)  layout.getChildAt(i);
            item.setTouchscreenBlocksFocus(false);
            item.setFocusable(false);
        }

    }

    //    初始化首页视频
    public void initIndexVideo(Context context, PlayerView playerView, String url){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (playerView == null || url.equals("")){
                    if (playerView == null){
                        playerView.setVisibility(View.GONE);
                    }
                }else {
                    playerView.setVisibility(View.VISIBLE);
                    // 隐藏控制面板
                    playerView.setUseController(false);
                    player = playerUtils.getStartPlayer(context,url);
                    // 隐藏控制面板
                    playerView.setUseController(false);
                    playerView.setPlayer(player);
                }

            }
        });
    }




    //    初始化开机图片或视频
    public void initStartVideoOrImg(Context context,String serverAddress,String tenant,
                                    ImageView logoView,ImageView bgView,PlayerView playerView){
        try{
            downLoadUtil = new DownLoadUtil(context);
            JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
            if (hotelMessageJson != null){
                String logoUrl = hotelMessageJson.getString("iconUrl");;
                String bgUrl = hotelMessageJson.getString("homepageBackground");
                String videoUrl = hotelMessageJson.getString("resourceUrl");
                Log.d(TAG, "initStartVideoOrImg: " + videoUrl);
//                初始化背景和logo
                initLogoAndBackGround(context,logoView,logoUrl,bgView,bgUrl);
                downLoadUtil.inspectOrDownloadFile(videoUrl, new DownLoadUtil.FileDownloadCallback() {
                    @Override
                    public void onFileReady(String filePath) {
                        initIndexVideo(context,playerView,filePath);
                    }
                });


            }
            JSONObject startData = hotelMessageJson.getJSONObject("startData");
//           判断是否需要开机动画
            if (startData.getInt("open") == 1){
                Intent intent = new Intent(context  , HospitalStartVideoActivity.class);
                intent.putExtra("startType",startData.getInt("type"));
                intent.putExtra("startUrl",startData.getString("url"));
                intent.putExtra("startSecond",startData.getLong("second"));
                intent.putExtra("startIsOpenTxt",startData.getInt("openTxt"));
                intent.putExtra("startContent",startData.getString("content"));
                startActivity(intent);
            }


        }catch (JSONException e){
            Log.e(TAG, "initStartVideoOrImg: ", e);
        }
    }
}
