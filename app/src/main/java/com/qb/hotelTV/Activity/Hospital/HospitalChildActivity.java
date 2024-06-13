package com.qb.hotelTV.Activity.Hospital;

import static com.qb.hotelTV.Utils.TimeUtil.getCurrentDateTime;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Activity.Hospital.fgm.VideoFragment;
import com.qb.hotelTV.Adaptor.FgmAdaptor;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.LayoutHospitalChildBinding;
import java.util.ArrayList;
import java.util.List;

public class HospitalChildActivity extends BaseActivity {
    private LayoutHospitalChildBinding layoutHospitalChildBinding;
    private Handler handler = new Handler();

    private List<Fragment> fragments = new ArrayList<>();

    private FocusScaleListener focusScaleListener = new FocusScaleListener();
    private FgmAdaptor fgmAdaptor;
    private ArrayList<CmsMessageModel> cms = new ArrayList<>();

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
        initComeBack();
    }


    private void initUI(){
        layoutHospitalChildBinding = DataBindingUtil.setContentView(HospitalChildActivity.this, R.layout.layout_hospital_child);
        startUpdateTask();
        initFocus();
        initFgmPage();
        String bg = getIntent().getStringExtra("bg");
        String roomNumber = getIntent().getStringExtra("roomNumber");
        String logo = getIntent().getStringExtra("logo");
        String weather = getIntent().getStringExtra("weather");
        String currentLocation = getIntent().getStringExtra("title");
        if (bg != null) {
            Glide.with(HospitalChildActivity.this)
                    .load(bg)
                    .error(R.drawable.app_bg)
                    .into(layoutHospitalChildBinding.hospitalChildBackground);
        }

        if (logo != null){
            Glide.with(HospitalChildActivity.this)
                    .load(logo)
                    .error(R.drawable.app_bg)
                    .into(layoutHospitalChildBinding.hospitalChildLogo);
        }
        layoutHospitalChildBinding.hospitalChildWeather.setText(weather);
        layoutHospitalChildBinding.hospitalChildRoomNumber.setText(roomNumber);
        layoutHospitalChildBinding.hospitalWebCurrentLocation.setText(currentLocation);
//        layoutHospitalChildBinding.hospitalChildViewPager.set

    }
    private void initFgmPage(){
//        fragments.add(WebFragment.newInstance("web","0"));
        fragments.add(VideoFragment.newInstance("video","0"));
        int type  = getIntent().getIntExtra("type",1);

        if (type == 0){
            layoutHospitalChildBinding.hospitalWebContact.setVisibility(View.VISIBLE);
            layoutHospitalChildBinding.hospitalChildViewPager.setVisibility(View.INVISIBLE);
            int id = getIntent().getIntExtra("id",-1);
            String serverAddress = getIntent().getStringExtra("serverAddress");
            String tenant =getIntent().getStringExtra("tenant");
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
                                layoutHospitalChildBinding.hospitalWebContact.loadDataWithBaseURL(null,strHtml,"text/html", "UTF-8", null);
                                layoutHospitalChildBinding.hospitalWebContact.requestFocus();
                                layoutHospitalChildBinding.hospitalWebContact.setOnFocusChangeListener(focusScaleListener);

                            }else {
                                layoutHospitalChildBinding.hospitalWebContact.setVisibility(View.GONE);
                            }
                        }
                    });
                }

                @Override
                public void onCmsMessageFailure(int code, String msg) {

                }
            });

        }else {
            layoutHospitalChildBinding.hospitalWebContact.setVisibility(View.GONE);
            fgmAdaptor = new FgmAdaptor(getSupportFragmentManager(),getLifecycle(),fragments);
            layoutHospitalChildBinding.hospitalChildViewPager.setAdapter(fgmAdaptor);
            layoutHospitalChildBinding.hospitalChildViewPager.setCurrentItem(0,false);
        }


    }

//    设置时间
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
                layoutHospitalChildBinding.hospitalChildDate.setText(datePart);
                layoutHospitalChildBinding.hospitalChildTime.setText(timePart);

                // 间隔一段时间后再次执行任务（这里设置为每秒更新一次）
                handler.postDelayed(this, 1000);
            }
        };

        // 执行第一次任务
        handler.post(updateTask);
    }

    private void initComeBack(){

        layoutHospitalChildBinding.hospitalChildComeback.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalChildBinding.hospitalChildIndex.setOnFocusChangeListener(focusScaleListener);
        layoutHospitalChildBinding.hospitalChildComeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        layoutHospitalChildBinding.hospitalChildIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initFocus(){
        layoutHospitalChildBinding.hospitalChildViewPager.requestFocus();
        layoutHospitalChildBinding.hospitalChildViewPager.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            layoutHospitalChildBinding.hospitalChildComeback.requestFocus();
                            return true; // 消费事件
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            layoutHospitalChildBinding.hospitalChildIndex.requestFocus();
                            return true; // 消费事件
                    }
                }
                return false;
            }
        });
    }


}
