package com.qb.hotelTV.Activity.Hospital;

import static com.qb.hotelTV.Utils.TimeUtil.getCurrentDateTime;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Adaptor.common.CommonAdapter;
import com.qb.hotelTV.Adaptor.common.CommonViewHolder;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.LayoutHospitalChildBinding;
import java.util.ArrayList;

public class HospitalChildActivity extends BaseActivity {
    private LayoutHospitalChildBinding layoutHospitalChildBinding;
    private Handler handler = new Handler();
    private final String TAG = "HospitalChildActivity";
    private FocusScaleListener focusScaleListener = new FocusScaleListener();
    private ArrayList<CmsMessageModel> cms = new ArrayList<>();
    private SimpleExoPlayer player ;
    private int currentPageNo = 1;
    CommonAdapter<CmsMessageModel> cmsMessageModelCommonAdapter;
    GridLayoutManager gridLayoutManager;

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
        initMainUI();
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

    }
    private void initMainUI(){
        int type  = getIntent().getIntExtra("type",1);
        int id = getIntent().getIntExtra("id",-1);
        Log.d(TAG, "initFgmPage: " + id);
        String serverAddress = getIntent().getStringExtra("serverAddress");
        String tenant =getIntent().getStringExtra("tenant");
//        先把全部隐藏
        initViewVisibility();
//        从上个界面拿参数先

        switch (type){
            case 0:
                layoutHospitalChildBinding.hospitalWebContact.setVisibility(View.VISIBLE);
                layoutHospitalChildBinding.hospitalWebContact.requestFocus();
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
                break;
            case 1:
                Log.d(TAG, "initFgmPage: ");

                layoutHospitalChildBinding.hospitalChildRecycler.setVisibility(View.VISIBLE);
                layoutHospitalChildBinding.hospitalChildUp.setVisibility(View.VISIBLE);
                layoutHospitalChildBinding.hospitalChildNext.setVisibility(View.VISIBLE);
                BackstageHttp.getInstance().getCmsMessage(serverAddress, tenant, id, currentPageNo, new BackstageHttp.CmsMessageCallBack() {
                    @Override
                    public void onCmsMessageResponse(ArrayList<CmsMessageModel> cmsMessageModels) {
                        cms.clear();
                        cms.addAll(cmsMessageModels);
                        Log.d(TAG, "onCmsMessageResponse: " + cms);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                cmsMessageModelCommonAdapter = new CommonAdapter<CmsMessageModel>(HospitalChildActivity.this,cms,R.layout.item_pic_list) {
                                    @Override
                                    public void bindData(CommonViewHolder holder, CmsMessageModel data, int position) {
                                        Glide.with(HospitalChildActivity.this)
                                                .load(cms.get(position).getPicUrl())
                                                .error(R.color.white)
                                                .into((ImageView) holder.getView(R.id.pic_list_logo));
                                    }
                                };


                                layoutHospitalChildBinding.hospitalChildRecycler.setAdapter(cmsMessageModelCommonAdapter);
                            }
                        });

                    }

                    @Override
                    public void onCmsMessageFailure(int code, String msg) {

                    }
                });
                break;
            case 2:
                layoutHospitalChildBinding.hospitalChildVideo.setVisibility(View.VISIBLE);
                layoutHospitalChildBinding.hospitalChildVideo.requestFocus();
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
                                    player = new SimpleExoPlayer.Builder(HospitalChildActivity.this).build();
//                                绑定player
                                    layoutHospitalChildBinding.hospitalChildVideo.setPlayer(player);
                                    // 隐藏控制面板
                                    layoutHospitalChildBinding.hospitalChildVideo.setUseController(false);
//                                设置循环播放
//            player.setRepeatMode(Player.REPEAT_MODE_ALL);
                                    MediaItem mediaItem = MediaItem.fromUri(url);
                                    player.setMediaItem(mediaItem);
                                    player.prepare();
                                    player.play();
                                }
                            }
                        });
                    }

                    @Override
                    public void onCmsMessageFailure(int code, String msg) {

                    }
                });
                break;
            default:
                break;
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

    private void initViewVisibility(){
        layoutHospitalChildBinding.hospitalWebContact.setVisibility(View.INVISIBLE);
        layoutHospitalChildBinding.hospitalChildVideo.setVisibility(View.INVISIBLE);
        layoutHospitalChildBinding.hospitalChildRecycler.setVisibility(View.INVISIBLE);
        layoutHospitalChildBinding.hospitalChildUp.setVisibility(View.INVISIBLE);
        layoutHospitalChildBinding.hospitalChildNext.setVisibility(View.INVISIBLE);
    }



}
