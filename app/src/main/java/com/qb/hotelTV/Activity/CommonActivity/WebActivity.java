package com.qb.hotelTV.Activity.CommonActivity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

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

public class WebActivity extends BaseActivity {
    private final String TAG = "WebActivity";
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
        String[] data = commonData.getData();
        serverAddress = data[0];
        tenant =data[1];
        roomNumber = data[2];

        id = getIntent().getLongExtra("id",-1);
        title = getIntent().getStringExtra("title");
        strHtml = getIntent().getStringExtra("content");
        layoutHospitalWebBinding.hospitalTop.setRoomNumber(roomNumber);
        layoutHospitalWebBinding.bottomBar.setTitle(title);

        layoutHospitalWebBinding.bottomBar.setIndexOnclickListener(new BottomBar.IndexOnclickListener() {
                @Override
                public void onIndexOnclickListener() {
                    // 返回 RESULT_OK 结果
                    setResult(RESULT_OK);
                }
            });
//        layoutHospitalWebBinding.hospitalWebContact.setBackgroundColor(0); // 背景透明
        layoutHospitalWebBinding.hospitalWebContact.setBackgroundColor(0); // 背景透明
        layoutHospitalWebBinding.hospitalWebContact.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null); // 软渲染确保透明效果
        layoutHospitalWebBinding.hospitalWebContact.getBackground().setAlpha(0); // 设置填充透明度 范围：0-255


//      获取天气和地址
        getGeoAndWeather(null,layoutHospitalWebBinding.hospitalTop.weather());
//        获取接口数据
        try{
            getData();
        }catch (Exception e){
            Log.e(TAG, "initUI: ", e);
        }


//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                if (GEO&&WEATHER&&HOTEL_MESSAGE){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Log.d(TAG, "run: xxxxxs" );
//                            if (strHotelLogo!= null){
//                                layoutHospitalWebBinding.hospitalTop.initOrUpdateTopBar(strHotelLogo,roomNumber,weather);
//                            }
//
//                            // 设置背景
//                            if (strHotelBg != null){
//                                Glide.with(WebActivity.this)
//                                        .load(strHotelBg)
//                                        .error(R.drawable.app_bg)
//                                        .into(layoutHospitalWebBinding.hospitalBackground);
//
//                            }
//                            layoutHospitalWebBinding.bottomBar.setTitle(title);
//                        }
//                    });
//
//
//                    timer.cancel();
//                }
//            }
//        };
//
//        timer.schedule(timerTask,0,1000);

    }



    private void getData() throws JSONException {
        JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
        if (hotelMessageJson != null){
            String logoUrl = hotelMessageJson.getString("iconUrl");;
            String bgUrl = hotelMessageJson.getString("homepageBackground");
//                初始化背景和logo
            initLogoAndBackGround(WebActivity.this,layoutHospitalWebBinding.hospitalTop.logo(),logoUrl,layoutHospitalWebBinding.hospitalBackground,bgUrl);
        }
        if (strHtml != null && !strHtml.equals("")){

            Log.d(TAG, "getData: " +strHtml);
            webViewAction(strHtml);
        }else {
            BackstageHttp.getInstance().getCmsMessage(serverAddress, tenant, id, new BackstageHttp.CmsMessageCallBack() {
                @Override
                public void onCmsMessageResponse(ArrayList<CmsMessageModel> cmsMessageModels) {
                    cms.clear();
                    cms.addAll(cmsMessageModels);
                    strHtml = cms.get(0).getContent();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (strHtml != null && !strHtml.equals("")){
                                Log.d(TAG, "getData: " +strHtml);
                                webViewAction(strHtml);
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

    private void webViewAction(String html){

        layoutHospitalWebBinding.hospitalWebContact.loadDataWithBaseURL(null,html,"text/html","utf-8",null);
        layoutHospitalWebBinding.hospitalWebContact.requestFocus();
        layoutHospitalWebBinding.hospitalWebContact.setNextFocusLeftId(layoutHospitalWebBinding.bottomBar.hospitalComebackId());
        layoutHospitalWebBinding.hospitalWebContact.setNextFocusRightId(layoutHospitalWebBinding.bottomBar.hospitalIndexId());
        layoutHospitalWebBinding.hospitalWebContact.setOnFocusChangeListener(focusScaleListener);
    }

    private void finishActivityFromChild(Activity activity) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        activity.finish();
    }



}
