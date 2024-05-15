package com.qb.hotelTV.Activity;

import static com.qb.hotelTV.Utils.LoadUtils.dismissProgressDialog;
import static com.qb.hotelTV.Utils.LoadUtils.showProgressDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.maning.updatelibrary.InstallUtils;
import com.qb.hotelTV.Adaptor.ApkAdaptor;
import com.qb.hotelTV.Const;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Setting.DownloadSetting;

import com.qb.hotelTV.Setting.ProgressDialogSetting;
import com.qb.hotelTV.Utils.LoadUtils;
import com.qb.hotelTV.databinding.LayoutAppBinding;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class AppActivity extends BaseActivity{
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
        if (bg != null) {
            Glide.with(AppActivity.this)
                    .load(bg)
                    .error(R.drawable.app_bg)
                    .into(layoutAppBinding.appBackground);
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
