package com.qb.hotelTV.Activity.Hospital;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Handler.CrashHandler;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Http.LocationHttp;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.LayoutHospitaWebBinding;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HospitalWebActivity extends BaseActivity {
    private final String TAG = "HospitalWebActivity";
    private boolean GEO=false,WEATHER=false,HOTEL_MESSAGE=false,WEB= false;
    private String geo,weather,locationString;
    private LayoutHospitaWebBinding layoutHospitaWebBinding;
    private String strHotelLogo,strHotelBg;

    private String serverAddress,tenant,roomNumber,title;
    private int id;
    private ArrayList<CmsMessageModel> cms = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        闪退日志
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

        initUI();
    }

    private void initUI(){
//        绑定布局
        layoutHospitaWebBinding = DataBindingUtil.setContentView(this, R.layout.layout_hospita_web);
//        获取基础信息
        roomNumber = getIntent().getStringExtra("roomNumber");
        serverAddress = getIntent().getStringExtra("serverAddress");
        tenant =getIntent().getStringExtra("tenant");
        id = getIntent().getIntExtra("id",-1);
        title = getIntent().getStringExtra("title");

//        获取天气
        locationString = getLocation();
        getGeoAndWeather(locationString);
//        获取接口数据
        getData();

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (GEO&&WEATHER&&HOTEL_MESSAGE&&WEB){
                    if (strHotelLogo!= null){
                        layoutHospitaWebBinding.hospitalTop.initOrUpdateTopBar(strHotelLogo,roomNumber,weather);
                    }

                    // 设置背景
                    if (strHotelBg != null){
                        Glide.with(HospitalWebActivity.this)
                                .load(strHotelBg)
                                .error(R.drawable.app_bg)
                                .into(layoutHospitaWebBinding.hospitalBackground);

                    }
                    layoutHospitaWebBinding.bottomBar.setTitle(title);

                    timer.cancel();
                }
            }
        };

    }



    //    获取坐标

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

    private void getData(){
        if (!HOTEL_MESSAGE){
            BackstageHttp.getInstance().getHotelMessage(serverAddress, tenant, new BackstageHttp.HotelMessageCallback() {
                @Override
                public void onHotelMessageResponse(String hotelName, String hotelLogo, String hotelBackground,String resourceUrl,String detail, String videoUrl) {
                    strHotelLogo = hotelLogo;
                    strHotelBg = hotelBackground;
                    HOTEL_MESSAGE = true;
                }
                @Override
                public void onHotelMessageFailure(int code, String msg) {
                    strHotelBg = "";
                    HOTEL_MESSAGE = true;
                }
            });
        }
        if (!WEB){
            BackstageHttp.getInstance().getCmsMessage(serverAddress, tenant, id, new BackstageHttp.CmsMessageCallBack() {
                @Override
                public void onCmsMessageResponse(ArrayList<CmsMessageModel> cmsMessageModels) {
                    cms.clear();
                    cms.addAll(cmsMessageModels);
                    String strHtml = cms.get(0).getContent();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (strHtml != null && !strHtml.equals("")){
                                layoutHospitaWebBinding.hospitalWebContact.loadDataWithBaseURL(null,strHtml,"text/html", "UTF-8", null);
                                layoutHospitaWebBinding.hospitalWebContact.requestFocus();
                            }else {
                                layoutHospitaWebBinding.hospitalWebContact.setVisibility(View.GONE);
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
}
