package com.qb.hotelTV.Activity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.qb.hotelTV.Activity.Hospital.HospitalStartVideoActivity;
import com.qb.hotelTV.R;

public class SocketNoticeActivity extends BaseActivity{

    private ImageView imageView;
    private SimpleExoPlayer player ;
    private PlayerView playerView;
    
    private String url;
    private int type;
    private long second;
    private Handler handler;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        // 禁用除了加减音量和开关机之外的其他按键
        View decorView = getWindow().getDecorView();
        decorView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    // 允许的按键
                    case KeyEvent.KEYCODE_VOLUME_UP:
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                    case KeyEvent.KEYCODE_POWER:
                        // 允许的事件
                        return false;
                    default:
                        // 禁用其他所有按键事件
                        return true;
                }
            }
        });
    }

    private void initUI(){
        setContentView(R.layout.layout_socket_notice);
        playerView = findViewById(R.id.notice_video);
        imageView = findViewById(R.id.notice_img);
        url =getIntent().getStringExtra("url");
        type = getIntent().getIntExtra("type",1);
        second = getIntent().getLongExtra("second",0);
        startEvent();
        keepTime();
    }
    
    private void startEvent(){
        switch (type){
            case 1:
                showVideo();
                break;
            case 2:
                showImg();
                break;
        }
    }

    private void showImg(){
        imageView.setVisibility(View.VISIBLE);
        playerView.setVisibility(View.GONE);
        Glide.with(SocketNoticeActivity.this)
                .load(url)
                .error(R.drawable.app_bg)
                .into(imageView);
    }

    private void showVideo(){
        imageView.setVisibility(View.GONE);
        playerView.setVisibility(View.VISIBLE);
        player = new SimpleExoPlayer.Builder(SocketNoticeActivity.this).build();
//                                绑定player
        playerView.setPlayer(player);
        // 隐藏控制面板
        playerView.setUseController(false);
//                                设置循环播放
//                          player.setRepeatMode(Player.REPEAT_MODE_ALL);
//      加载视频，视频加载完成播放
        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }

    private void keepTime(){
        // 延迟10秒执行关闭操作
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 在这里编写您要执行的关闭操作
                finish();
            }
        }, second*1000); // 10000 毫秒 = 10 秒
    }
    
}
