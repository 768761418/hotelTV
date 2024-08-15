package com.qb.hotelTV.Activity.CommonActivity;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.LayoutHospitalVideoBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class VideoActivity extends BaseActivity {
    private boolean GEO=false,WEATHER=false,HOTEL_MESSAGE=false;
    private String geo,weather,locationString;
    private String serverAddress,tenant,roomNumber,title;
    private String strHotelLogo,strHotelBg, strHtml;
    private Long id;
    private LayoutHospitalVideoBinding layoutHospitalVideoBinding;
    private final String TAG = "VideoActivity";
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
//        获取通用请求数据
        String[] data = commonData.getData();
        serverAddress = data[0];
        tenant =data[1];
        roomNumber = data[2];

        id = getIntent().getLongExtra("id",-1);
        title = getIntent().getStringExtra("title");
        layoutHospitalVideoBinding.hospitalTop.setRoomNumber(roomNumber);
        layoutHospitalVideoBinding.bottomBar.setTitle(title);
//      获取天气和地址
        getGeoAndWeather(null,layoutHospitalVideoBinding.hospitalTop.weather());
        //        获取接口数据
        try{
            getData();
        }catch (Exception e){
            Log.e(TAG, "initUI: ", e);
        }


    }





    private void getData() throws JSONException {
        JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
        if (hotelMessageJson != null){
            String logoUrl = hotelMessageJson.getString("iconUrl");;
            String bgUrl = hotelMessageJson.getString("homepageBackground");
//                初始化背景和logo
            initLogoAndBackGround(VideoActivity.this,layoutHospitalVideoBinding.hospitalTop.logo(),logoUrl,layoutHospitalVideoBinding.hospitalBackground,bgUrl);
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
                            player = new SimpleExoPlayer.Builder(VideoActivity.this).build();
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
