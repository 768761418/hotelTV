package com.qb.hotelTV.Activity.Hospital;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Http.LocationHttp;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.LayoutHospitalVideoBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HospitalVideoActivity extends BaseActivity {
    private boolean GEO=false,WEATHER=false,HOTEL_MESSAGE=false;
    private String geo,weather,locationString;
    private String serverAddress,tenant,roomNumber,title;
    private String strHotelLogo,strHotelBg, strHtml;
    private Long id;
    private LayoutHospitalVideoBinding layoutHospitalVideoBinding;
    private final String TAG = "HospitalVideoActivity";
    private ArrayList<CmsMessageModel> cms = new ArrayList<>();
    private SimpleExoPlayer player ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (player != null) {
            player.pause(); // 在失去焦点时暂停视频
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player != null) {
            player.play(); // 在恢复焦点时继续播放视频
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release(); // 在关闭界面时销毁播放器
            player = null;
        }
    }

    private void initUI(){
        layoutHospitalVideoBinding = DataBindingUtil.setContentView(this, R.layout.layout_hospital_video);

        roomNumber = getIntent().getStringExtra("roomNumber");
        serverAddress = getIntent().getStringExtra("serverAddress");
        tenant =getIntent().getStringExtra("tenant");
        id = getIntent().getLongExtra("id",-1);
        title = getIntent().getStringExtra("title");
        //        获取天气
        locationString = getLocation();
        getGeoAndWeather(locationString);
        //        获取接口数据
        try{
            getData();
        }catch (Exception e){
            Log.e(TAG, "initUI: ", e);
        }


        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (GEO&&WEATHER&&HOTEL_MESSAGE){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "run: xxxxxs" );
                            if (strHotelLogo!= null){
                                layoutHospitalVideoBinding.hospitalTop.initOrUpdateTopBar(strHotelLogo,roomNumber,weather);
                            }

                            // 设置背景
                            if (strHotelBg != null){
                                Glide.with(HospitalVideoActivity.this)
                                        .load(strHotelBg)
                                        .error(R.drawable.app_bg)
                                        .into(layoutHospitalVideoBinding.hospitalBackground);

                            }
                            layoutHospitalVideoBinding.bottomBar.setTitle(title);
                        }
                    });


                    timer.cancel();
                }
            }
        };

        timer.schedule(timerTask,0,1000);
    }

    //    请求天气和温度
    private  void getGeoAndWeather(String locationString){
//        请求地址
        if (!GEO){
            LocationHttp.getInstance().getGeo(locationString, new LocationHttp.LocationHttpCallback() {
                @Override
                public void onResponse(String responseData) {
                    geo = responseData;
                    GEO = true;
                }

                @Override
                public void onFailure(String failName) {
                    geo = "";
                    GEO = true;
                }
            });
        }

//        请求天气
        if (!WEATHER){
            LocationHttp.getInstance().getWeather(locationString, new LocationHttp.LocationHttpCallback() {
                @Override
                public void onResponse(String responseData) {
                    weather = responseData;
                    WEATHER = true;
                }

                @Override
                public void onFailure(String failName) {
                    weather = "";
                    WEATHER = true;
                }
            });
        }
    }


    private void getData() throws JSONException {
        if (!HOTEL_MESSAGE){
            JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
            if (hotelMessageJson != null){
                strHotelLogo = hotelMessageJson.getString("iconUrl");;
                strHotelBg = hotelMessageJson.getString("homepageBackground");;
            }
            HOTEL_MESSAGE = true;
        }

        BackstageHttp.getInstance().getCmsMessage(serverAddress, tenant, id, new BackstageHttp.CmsMessageCallBack() {
            @Override
            public void onCmsMessageResponse(ArrayList<CmsMessageModel> cmsMessageModels) {
                cms.clear();
                cms.addAll(cmsMessageModels);
                String url = cms.get(0).getContent();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (url != null && !url.equals("")){
                            player = new SimpleExoPlayer.Builder(HospitalVideoActivity.this).build();
//                                绑定player
                            layoutHospitalVideoBinding.hospitalVideo.setPlayer(player);
                            // 隐藏控制面板
                            layoutHospitalVideoBinding.hospitalVideo.setUseController(false);
//                                设置循环播放
//                          player.setRepeatMode(Player.REPEAT_MODE_ALL);
                            MediaItem mediaItem = MediaItem.fromUri(url);
                            player.setMediaItem(mediaItem);
                            player.prepare();
                            player.play();


                            layoutHospitalVideoBinding.hospitalVideo.requestFocus();
                            layoutHospitalVideoBinding.hospitalVideo.setNextFocusLeftId(layoutHospitalVideoBinding.bottomBar.hospitalComebackId());
                            layoutHospitalVideoBinding.hospitalVideo.setNextFocusRightId(layoutHospitalVideoBinding.bottomBar.hospitalIndexId());
                        }
                    }
                });
            }

            @Override
            public void onCmsMessageFailure(int code, String msg) {

            }
        });

    }
}
