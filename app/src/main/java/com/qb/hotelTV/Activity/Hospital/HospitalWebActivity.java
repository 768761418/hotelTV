package com.qb.hotelTV.Activity.Hospital;

import static com.qb.hotelTV.Utils.TimeUtil.getCurrentDateTime;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Activity.HotelActivity;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.LayoutHospitalWebBinding;

import java.util.ArrayList;

public class HospitalWebActivity extends BaseActivity {

    private String serverAddress,tenant,roomNumber;
    private LayoutHospitalWebBinding layoutHospitalWebBinding;
    private FocusScaleListener focusScaleListener = new FocusScaleListener();
    private ArrayList<CmsMessageModel> cms = new ArrayList<>();
    private Handler handler = new Handler();
    private String strHtml,strTitle;

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
        init();
    }

    private void init(){
        layoutHospitalWebBinding = DataBindingUtil.setContentView(HospitalWebActivity.this, R.layout.layout_hospital_web);
        String bg = getIntent().getStringExtra("bg");
        int id = getIntent().getIntExtra("id",-1);
        serverAddress = getIntent().getStringExtra("serverAddress");
        tenant = getIntent().getStringExtra("tenant");
        roomNumber = getIntent().getStringExtra("roomNumber");
        String logo = getIntent().getStringExtra("logo");
        String weather = getIntent().getStringExtra("weather");


        startUpdateTask();
        comeback();
        if (bg != null) {
            Glide.with(HospitalWebActivity.this)
                    .load(bg)
                    .error(R.drawable.app_bg)
                    .into(layoutHospitalWebBinding.hotelBackground);
        }

        if (logo != null){
            Glide.with(HospitalWebActivity.this)
                    .load(logo)
                    .error(R.drawable.app_bg)
                    .into(layoutHospitalWebBinding.hospitalWebLogo);
        }
        layoutHospitalWebBinding.hospitalWebWeather.setText(weather);
        layoutHospitalWebBinding.hospitalWebRoomNumber.setText(roomNumber);


        if (id != -1 ){
            BackstageHttp.getInstance().getCmsMessage(serverAddress, tenant, id, new BackstageHttp.CmsMessageCallBack() {
                @Override
                public void onCmsMessageResponse(ArrayList<CmsMessageModel> cmsMessageModels) {
                    cms.clear();
                    cms.addAll(cmsMessageModels);
                    strHtml = cms.get(0).getContent();
//                            strHtml = "";
                    strTitle = cms.get(0).getTitle();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (strHtml != null && !strHtml.equals("")){
                                layoutHospitalWebBinding.hospitalWebContact.loadDataWithBaseURL(null,strHtml,"text/html", "UTF-8", null);
                                layoutHospitalWebBinding.hospitalWebContact.requestFocus();
                                layoutHospitalWebBinding.hospitalWebContact.setOnFocusChangeListener(focusScaleListener);
                              layoutHospitalWebBinding.hospitalWebCurrentLocation.setText(strTitle);
                            }else {
                                layoutHospitalWebBinding.hospitalWebContact.setVisibility(View.GONE);
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

    private void startUpdateTask() {
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                // 获取当前日期和时间
                String currentDateTimeString = getCurrentDateTime(1);
                // 分割日期和时间
                String[] parts = currentDateTimeString.split(" ");
                String datePart = parts[0];
                String timePart = parts[1];
                // 更新 TextView 的文本内容为当前日期和时间的各部分
                layoutHospitalWebBinding.hospitalWebDate.setText(datePart);
                layoutHospitalWebBinding.hospitalWebTime.setText(timePart);

                // 间隔一段时间后再次执行任务（这里设置为每秒更新一次）
                handler.postDelayed(this, 1000);
            }
        };

        // 执行第一次任务
        handler.post(updateTask);
    }

    private void comeback(){
        layoutHospitalWebBinding.hospitalWebComeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        layoutHospitalWebBinding.hospitalWebIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
