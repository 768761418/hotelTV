package com.qb.hotelTV.Activity.Hospital;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
import com.qb.hotelTV.databinding.LayoutHospitalWebBinding;
import com.qb.hotelTV.module.hospital.BottomBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HospitalWebActivity extends BaseActivity {
    private final String TAG = "HospitalWebActivity";
    private boolean GEO=false,WEATHER=false,HOTEL_MESSAGE=false,WEB= false;
    private String geo,weather,locationString;
    private LayoutHospitalWebBinding layoutHospitalWebBinding;
    private String strHotelLogo,strHotelBg, strHtml;

    private String serverAddress,tenant,roomNumber,title;
    private Long id;
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
        layoutHospitalWebBinding = DataBindingUtil.setContentView(this, R.layout.layout_hospital_web);
//        获取基础信息
        roomNumber = getIntent().getStringExtra("roomNumber");
        serverAddress = getIntent().getStringExtra("serverAddress");
        tenant =getIntent().getStringExtra("tenant");
        id = getIntent().getLongExtra("id",-1);
        title = getIntent().getStringExtra("title");
        strHtml = getIntent().getStringExtra("content");
        boolean isList = getIntent().getBooleanExtra("isList",false);

        layoutHospitalWebBinding.bottomBar.setIndexOnclickListener(new BottomBar.IndexOnclickListener() {
                @Override
                public void onIndexOnclickListener() {
                    // 返回 RESULT_OK 结果
                    setResult(RESULT_OK);
                }
            });


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
                                layoutHospitalWebBinding.hospitalTop.initOrUpdateTopBar(strHotelLogo,roomNumber,weather);
                            }

                            // 设置背景
                            if (strHotelBg != null){
                                Glide.with(HospitalWebActivity.this)
                                        .load(strHotelBg)
                                        .error(R.drawable.app_bg)
                                        .into(layoutHospitalWebBinding.hospitalBackground);

                            }
                            layoutHospitalWebBinding.bottomBar.setTitle(title);
                        }
                    });


                    timer.cancel();
                }
            }
        };

        timer.schedule(timerTask,0,1000);

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

    private void getData() throws JSONException {
        if (!HOTEL_MESSAGE){
            JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
            if (hotelMessageJson != null){
                strHotelLogo = hotelMessageJson.getString("iconUrl");;
                strHotelBg = hotelMessageJson.getString("homepageBackground");;
            }
            HOTEL_MESSAGE = true;
        }
        if (strHtml != null && !strHtml.equals("")){
            layoutHospitalWebBinding.hospitalWebContact.loadData(strHtml,"text/html","utf-8");
            layoutHospitalWebBinding.hospitalWebContact.requestFocus();
            layoutHospitalWebBinding.hospitalWebContact.setNextFocusLeftId(layoutHospitalWebBinding.bottomBar.hospitalComebackId());
            layoutHospitalWebBinding.hospitalWebContact.setNextFocusRightId(layoutHospitalWebBinding.bottomBar.hospitalIndexId());
            layoutHospitalWebBinding.hospitalWebContact.setOnFocusChangeListener(focusScaleListener);
        }else {
            BackstageHttp.getInstance().getCmsMessage(serverAddress, tenant, id, new BackstageHttp.CmsMessageCallBack() {
                @Override
                public void onCmsMessageResponse(ArrayList<CmsMessageModel> cmsMessageModels) {
                    cms.clear();
                    cms.addAll(cmsMessageModels);
                    strHtml = cms.get(0).getContent();
                    Log.d(TAG, "xxs: " +strHtml);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (strHtml != null && !strHtml.equals("")){
//                            设置加载内容和焦点
                                layoutHospitalWebBinding.hospitalWebContact.loadData(strHtml,"text/html","utf-8");
                                layoutHospitalWebBinding.hospitalWebContact.requestFocus();
                                layoutHospitalWebBinding.hospitalWebContact.setNextFocusLeftId(layoutHospitalWebBinding.bottomBar.hospitalComebackId());
                                layoutHospitalWebBinding.hospitalWebContact.setNextFocusRightId(layoutHospitalWebBinding.bottomBar.hospitalIndexId());
                                layoutHospitalWebBinding.hospitalWebContact.setOnFocusChangeListener(focusScaleListener);
                            }else {
                                layoutHospitalWebBinding.hospitalWebContact.setVisibility(View.GONE);
                            }

                        }
                    });
                }

                @Override
                public void onCmsMessageFailure(int code, String msg) {
                    WEB = true;
                }
            });
        }


    }

    private void finishActivityFromChild(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        activity.finish();
    }

}
