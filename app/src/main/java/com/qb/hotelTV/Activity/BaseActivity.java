package com.qb.hotelTV.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
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
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dreamgyf.android.ui.widget.textview.marquee.MarqueeTextView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.HttpUtil;
import com.qb.hotelTV.Activity.Hospital.HospitalActivity;
import com.qb.hotelTV.Activity.Hospital.HospitalListActivity;
import com.qb.hotelTV.Activity.Hospital.HospitalStartVideoActivity;
import com.qb.hotelTV.Activity.Hospital.HospitalVideoActivity;
import com.qb.hotelTV.Activity.Hospital.HospitalWebActivity;
import com.qb.hotelTV.Data.CommonData;
import com.qb.hotelTV.Handler.CrashHandler;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Http.LocationHttp;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Listener.WebSocketClient;
import com.qb.hotelTV.Model.HotelListModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.PermissionUtils;
import com.qb.hotelTV.huibuTv.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class BaseActivity extends Activity {
    public FocusScaleListener focusScaleListener = new FocusScaleListener();
    private JSONObject hotelMessage = null;
    public CommonData commonData = CommonData.getInstance();
    private String TAG = "BaseActivity";
    private Handler handler = new Handler();
    private boolean isGetToken = true;
    private Player player;

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
        // 闪退日志
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        //请求权限
        PermissionUtils permissionUtils = new PermissionUtils();
        permissionUtils.checkPermission(this);
    }

    //    获取经纬度
    public String getLocation(){
        String locationString = "";
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
                return locationString;
            }
            do{
                double latitude = lastKnownLocation.getLatitude();
                double longitude = lastKnownLocation.getLongitude();
                // 将经纬度保留两位小数并合成字符串
                locationString = String.format("%.2f,%.2f",  longitude, latitude);
                // 在此处处理获取到的经纬度信息
            } while(lastKnownLocation == null);
            return locationString;
        } catch (SecurityException e) {
            return locationString;
        }
    }
    //    请求天气和地址
    public  void getGeoAndWeather(TextView geoView,TextView weatherView){
        String locationString = getLocation();
//        请求地址
        LocationHttp.getInstance().getGeo(locationString, new LocationHttp.LocationHttpCallback() {
            @Override
            public void onResponse(String responseData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (geoView != null){
                            geoView.setText(responseData);
                        }
                    }
                });
            }

            @Override
            public void onFailure(String failName) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (geoView != null){
                            geoView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
//        请求天气
        LocationHttp.getInstance().getWeather(locationString, new LocationHttp.LocationHttpCallback() {
            @Override
            public void onResponse(String responseData) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weatherView != null){
                            weatherView.setText(responseData);
                        }
                    }
                });

            }

            @Override
            public void onFailure(String failName) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weatherView != null){
                            weatherView.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }


//    获取酒店配置
    public JSONObject getHotelMessageFromHttp (String serverAddress, String tenant){
        hotelMessage = null;
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                hotelMessage = BackstageHttp.getInstance().getHotelMessage(serverAddress, tenant);
                latch.countDown();
            }
        }).start();
//
        try {
            latch.await(); // 等待请求完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return hotelMessage;
    }

//    登录
    public void login(String serverAddress,String roomNumber,String tenant){
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                BackstageHttp.getInstance().loginSystem(serverAddress,roomNumber,tenant);
                latch.countDown();
            }
        }).start();
//
        try {
            latch.await(); // 等待请求完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


//    拼接websocket路径
    public String getWebSocketUrl(String serverAddress){
        String token = BackstageHttp.getInstance().getToken();
        if (serverAddress.startsWith("http://")) {
            return serverAddress.replace("http://", "ws://") + "/infra/ws?token=" + token;
        } else if (serverAddress.startsWith("https://")) {
            return serverAddress.replace("https://", "ws://") + "/infra/ws?token=" + token;
        }
        return null;
    }

//    初始化websocket
    public void initWebSocket(Context context, WebSocketClient webSocketClient, String url){
        webSocketClient = new WebSocketClient(url);
        Log.d(TAG, "initWebSocket: " + url);
        if (webSocketClient.isConnected()){
            Toast.makeText(context,"已连接至服务器socket",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context,"请检查服务器是否正常",Toast.LENGTH_SHORT).show();

        }

        webSocketClient.setMessageCallback(new WebSocketClient.MessageCallback() {
            @Override
            public void onMessageCallback(String data) {
                try{
                    Log.d(TAG, "initWebSocket: " + data);
                    JSONObject jsonObject = new JSONObject(data);
                    String type = jsonObject.getString("type");
                    if (type.equals("insert-notice")){
//                        由于websocket二级是字符串所以先拿字符串然后转成json
                        String strContent = jsonObject.getString("content");
                        JSONObject contentObject = new JSONObject(strContent);
//                        获取播放信息
                        String content= contentObject.getString("content");
//                        配置信息
                        JSONObject configData = contentObject.getJSONObject("configData");
                        int webType = configData.getInt("type");
                        long webSecond = configData.getLong("second");
                        Intent intent = new Intent(context, SocketNoticeActivity.class);
                        intent.putExtra("url",content);
                        intent.putExtra("type",webType);
                        intent.putExtra("second",webSecond);
                        startActivity(intent);
                    }
                }catch (JSONException e){
                    Log.e(TAG, "onMessageCallback: " + data, e );
                }
            }
        });
        sendPingToServer(webSocketClient);
    }
//    持续发ping给服务器实现长连接
    private void sendPingToServer(WebSocketClient webSocketClient){
        Runnable sendMessageToServer = new Runnable() {
            @Override
            public void run() {
                webSocketClient.sendMessage("ping");
                Log.d(TAG, "run: ping");

                handler.postDelayed(this, 60*1000);
            }
        };
        handler.post(sendMessageToServer);
    }

//    获取公告并修改组件
    public void getAnnouncements(String serverAddress,String tenant,MarqueeTextView view){
        BackstageHttp.getInstance().getTvText(serverAddress, tenant, new BackstageHttp.TvTextCallback() {
            @Override
            public void onTvTextResponse(String tvText, String tvTextColor,int code) {
                Log.d(TAG, "onTvTextResponse: " + tvText );
                Log.d(TAG, "onTvTextResponse: " +tvTextColor );
//                主线程修改公告
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

            }

            @Override
            public void onTvTextFailure(int code, String msg) {
                view.setVisibility(View.GONE);
            }
        });
    }

//    初始化logo和background
    public void initLogoAndBackGround(Context context,ImageView logoView,String logoUrl,ImageView bgView,String bgUrl){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (logoView == null || logoUrl.equals("")){
                    if (logoView != null){
                        logoView.setVisibility(View.GONE);
                    }
                }else {
                    Glide.with((Activity) context)
                            .load(logoUrl)
                            .error(R.drawable.img)
                            .into(logoView);
                }

                if (bgView == null || bgUrl.equals("")){
                    if (bgView != null){
                        bgView.setVisibility(View.GONE);
                    }
                }else {
                    Glide.with((Activity) context)
                            .load(bgUrl)
                            .error(R.drawable.app_bg)
                            .into(bgView);
                }
            }
        });
    }

//    初始化首页视频
    public void initIndexVideo(Context context,PlayerView playerView,String url){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (playerView == null || url.equals("")){
                    if (playerView == null){
                        playerView.setVisibility(View.GONE);
                    }
                }else {
                    playerView.setVisibility(View.VISIBLE);
                    player = new SimpleExoPlayer.Builder(context).build();
//                                绑定player
                    playerView.setPlayer(player);
                    // 隐藏控制面板
                    playerView.setUseController(false);
//                                设置循环播放
                    player.setRepeatMode(Player.REPEAT_MODE_ALL);
                    MediaItem mediaItem = MediaItem.fromUri(url);
                    player.setMediaItem(mediaItem);
                    player.prepare();
                    player.play();
                }

            }
        });
    }

    //    默认携带参数
    private void defaultPutIntent(Intent intent,String title,Long id){
        intent.putExtra("title",title);
        intent.putExtra("id",id);
    }
    //    界面点击模块事件
    public  <T extends ViewGroup> void indexListOnclick(Context context, T layout, ArrayList<HotelListModel> hotelListModels,String theme){
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < hotelListModels.size(); i++) {
                        Log.d(TAG, "onApkResponse: " + hotelListModels.get(i).getName());
                        Log.d(TAG, "onApkResponse: " + hotelListModels.get(i).getBackgroundUrl());
                        Log.d(TAG, "onApkResponse: " + hotelListModels.get(i).getPicUrl());
                        Log.d(TAG, "onApkResponse: " + hotelListModels.get(i).getType());

                        //获取到每一个item的layout替换掉图片和文字和跳转地址
                        LinearLayout item = (LinearLayout)  layout.getChildAt(i);

                        ImageView img  = item.findViewById(R.id.item_img);

                        Glide.with((Activity) context)
                                .load(hotelListModels.get(i).getPicUrl())
                                .error(R.color.black)
                                .into(img);

                        ((TextView)item.findViewById(R.id.item_text)).setText(hotelListModels.get(i).getName());

                        if (theme.equals("hospital")){
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


                        }

                    }
                }
            });
        }catch (Exception e){

        }

    }


    //    初始化启动图片或视频
    public void initStartVideoOrImg(Context context,String serverAddress,String tenant,
                                     ImageView logoView,ImageView bgView,PlayerView playerView){
        try{
            JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
            String themeType = "hospital1";
            if (hotelMessageJson != null){
                String logoUrl = hotelMessageJson.getString("iconUrl");;
                String bgUrl = hotelMessageJson.getString("homepageBackground");
                String videoUrl = hotelMessageJson.getString("resourceUrl");
//                初始化背景和logo
                initLogoAndBackGround(context,logoView,logoUrl,bgView,bgUrl);
                initIndexVideo(context,playerView,videoUrl);

                themeType = hotelMessageJson.getString("themeType");
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
            // TODO 切换主题
            if (themeType.equals("hospital1")){

            }


        }catch (JSONException e){
            Log.e(TAG, "initStartVideoOrImg: ", e);
        }
    }

}
