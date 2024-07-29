package com.qb.hotelTV.Activity.Hospital;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.R;

public class HospitalStartVideoActivity extends BaseActivity {
    private SimpleExoPlayer player ;
    private PlayerView playerView;
    private ImageView imageView,imageBg;
    private LinearLayout nextBtn;
    private String url,startContent ;
    private int startType,startIsOpenTxt;
    private long startSecond = 10;
    private WebView webView;
    private Handler handler = new Handler();
    private  String TAG = "HospitalStartVideoActivity";
    private boolean isShow = false;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: " + isShow);
        if (isShow){
            Log.d(TAG, "onKeyDown: ddd");
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_SPACE || keyCode == KeyEvent.KEYCODE_ENTER){
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();

    }

    private void initUI(){
        setContentView(R.layout.layout_hospital_start_video);
        playerView = findViewById(R.id.hospital_start_video);
        imageView = findViewById(R.id.hospital_start_img);
        nextBtn = findViewById(R.id.hospital_next_btn);
        webView = findViewById(R.id.hospital_web);
        imageBg = findViewById(R.id.hospital_web_bg);
        onClickNextBtn();
//        类型
        startType = getIntent().getIntExtra("startType",1);
//        地址
        url = getIntent().getStringExtra("startUrl");
//        持续秒速
        startSecond =  getIntent().getLongExtra("startSecond",0);
//        是否渲染web
        startIsOpenTxt = getIntent().getIntExtra("startIsOpenTxt",0);
        startContent = getIntent().getStringExtra("startContent");



        Log.d(TAG, "initUI: " +startType);
//        判断是否显示
        // 1视频 2图片
        if (startType == 1){
            videoStart();
        } else if (startType == 2) {
            imgStart();
        }



    }

//    图片展示10s后进入下一页
    private void startActivityAfterDelay() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                closeThisActivity();
            }
        }, startSecond*1000); // 延迟10秒（10000毫秒）
    }

//    十秒之后出现一个按钮进入下一页
    private void showNextBtn(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextBtn.setVisibility(View.VISIBLE);
                nextBtn.clearFocus();
                nextBtn.requestFocus();
                isShow = true;
                nextBtn.setOnFocusChangeListener(focusScaleListener);
               Log.d(TAG, "run:111 ");
            }
        }, 10000); // 延迟10秒（10000毫秒）
    }

    private void onClickNextBtn(){
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

//    视频启动方法
    private void videoStart(){

        imageView.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);
        player = new SimpleExoPlayer.Builder(HospitalStartVideoActivity.this).build();
//                                绑定player
        playerView.setPlayer(player);
        // 隐藏控制面板
        playerView.setUseController(false);
//                                设置循环播放
//                          player.setRepeatMode(Player.REPEAT_MODE_ALL);
        showNextBtn();
//        加载视频，视频加载完成播放
        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

//        如果播完了就进入第一页
        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == Player.STATE_ENDED){
                    closeThisActivity();
                }
            }
        });
    }

    private void closeThisActivity(){
        finish();
    }

//    图片启动方法
    private void imgStart(){
        imageView.setVisibility(View.VISIBLE);

        playerView.setVisibility(View.GONE);
        if (startIsOpenTxt == 1){
            imageBg.setVisibility(View.VISIBLE);
            webView.setVisibility(View.VISIBLE);
            webView.setBackgroundColor(0); // 背景透明
            webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null); // 软渲染确保透明效果
            webView.loadData(startContent,"text/html","utf-8");
        }

        showNextBtn();
        Glide.with(HospitalStartVideoActivity.this)
                .load(url)
                .error(R.drawable.app_bg)
                .into(imageView);
        startActivityAfterDelay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.setPlayWhenReady(false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }

}
