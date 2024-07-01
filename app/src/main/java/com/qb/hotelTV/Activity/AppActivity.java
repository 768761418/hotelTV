package com.qb.hotelTV.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Activity.Hospital.HospitalActivity;
import com.qb.hotelTV.Adaptor.ApkAdaptor;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.R;

import com.qb.hotelTV.databinding.LayoutAppBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AppActivity extends BaseActivity {
    private LayoutAppBinding layoutAppBinding;
    private final String TAG = "AppActivity";
    private boolean apkIsGet = false;

    String serverAddress,tenant;
    private ApkAdaptor apkAdaptor;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish(); // 结束当前活动
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();

    }

    private void initUI() {
        layoutAppBinding = DataBindingUtil.setContentView(this, R.layout.layout_app);
        String title = getIntent().getStringExtra("title");
        String[] data = commonData.getData();
        serverAddress = data[0];
        tenant =data[1];


        int type = getIntent().getIntExtra("type",0);
        if (type == 1){
            layoutAppBinding.bottomBar.setVisibility(View.GONE);
        }

        try {
            JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
            String themeType = "hospital1";
            if (hotelMessageJson != null){
                String logoUrl = hotelMessageJson.getString("iconUrl");;
                String bgUrl = hotelMessageJson.getString("homepageBackground");
//                初始化背景和logo
                initLogoAndBackGround(AppActivity.this,null,logoUrl,layoutAppBinding.appBackground,bgUrl);
            }
        }catch (JSONException e){
            Log.e(TAG, "initUI: ", e);
        }

        if (title != null){
            layoutAppBinding.appTitle.setText(title);
            layoutAppBinding.bottomBar.setTitle(title);
        }
        getApkList();
    }


    private void getApkList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<ApkModel> apkModelArrayList =  BackstageHttp.getInstance().getApk(serverAddress, tenant);
                if (!apkModelArrayList.isEmpty()){
                    apkAdaptor = new ApkAdaptor(AppActivity.this, apkModelArrayList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layoutAppBinding.appApkList.setAdapter(apkAdaptor);
                            apkAdaptor.notifyDataSetChanged();
                        }
                    });

                }
            }
        }).start();

    }



//    类括号
}
