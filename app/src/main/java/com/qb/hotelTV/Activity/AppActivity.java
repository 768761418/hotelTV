package com.qb.hotelTV.Activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Adaptor.ApkAdaptor;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.R;

import com.qb.hotelTV.databinding.LayoutAppBinding;

import java.util.ArrayList;

public class AppActivity extends BaseActivity {
    private LayoutAppBinding layoutAppBinding;
    private final String TAG = "AppActivity";
    private boolean apkIsGet = false;
    //    用来存放apk的列表
    private ArrayList<ApkModel> apkList = new ArrayList<>();;

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
        String bg = getIntent().getStringExtra("bg");
        String title = getIntent().getStringExtra("title");
        int type = getIntent().getIntExtra("type",0);
        if (type == 1){
            layoutAppBinding.bottomBar.setVisibility(View.GONE);
        }
        if (bg != null) {
            Glide.with(AppActivity.this)
                    .load(bg)
                    .error(R.drawable.app_bg)
                    .into(layoutAppBinding.appBackground);
        }
        if (title != null){
            layoutAppBinding.appTitle.setText(title);
            layoutAppBinding.bottomBar.setTitle(title);
        }
        getApkList();
    }


    private void getApkList(){
        serverAddress = getIntent().getStringExtra("serverAddress");
        tenant = getIntent().getStringExtra("tenant");
        BackstageHttp.getInstance().getApk(serverAddress, tenant, new BackstageHttp.ApkCallback() {
            @Override
            public void onApkResponse(ArrayList<ApkModel> apkModelArrayList) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (apkModelArrayList != null && !apkModelArrayList.isEmpty()) {
                            apkList.clear();
                            apkList.addAll(apkModelArrayList);
                            apkAdaptor = new ApkAdaptor(AppActivity.this, apkList);
                            layoutAppBinding.appApkList.setAdapter(apkAdaptor);
                            apkAdaptor.notifyDataSetChanged();
                        }
                    }
                });

            }
            @Override
            public void onApkFailure(int code, String msg) {
                apkAdaptor.notifyDataSetChanged();
                apkIsGet =true;
            }
        });
    }



//    类括号
}
