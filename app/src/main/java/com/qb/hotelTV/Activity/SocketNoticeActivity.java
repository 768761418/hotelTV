package com.qb.hotelTV.Activity;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
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

//    注册广播
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("com.qb.hotel.ACTION_START_FINISH_ACTIVITY")) {
                finish();
            }
        }
    };


//    设置只有音量键和关机键有效
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            // 允许的按键
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_POWER:
                // 允许的事件
                return false;
            default:
                return  true;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(receiver);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter filter = new IntentFilter("com.qb.hotel.ACTION_START_FINISH_ACTIVITY");
        broadcastManager.registerReceiver(receiver, filter);
        initUI();

    }

    private void initUI(){
        setContentView(R.layout.layout_socket_notice);
        playerView = findViewById(R.id.notice_video);
        imageView = findViewById(R.id.notice_img);
        url =getIntent().getStringExtra("url");
        type = getIntent().getIntExtra("type",1);
        second = getIntent().getLongExtra("second",0);
        startEvent();
    }
    
    private void startEvent(){
        switch (type){
            case 1:
                showVideo();
                keepTime();
                break;
            case 3:
                showVideo();
                break;
            case 2:
                showImg();
                keepTime();
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

        if (url.startsWith("rtsp")){
            // 创建 RTSP 媒体源
            MediaSource mediaSource = new RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(url));
            player.prepare(mediaSource);
        }else {
            //      加载视频，视频加载完成播放
            MediaItem mediaItem = MediaItem.fromUri(url);
            player.setMediaItem(mediaItem);
            player.prepare();
        }
//                                设置循环播放
//                          player.setRepeatMode(Player.REPEAT_MODE_ALL);


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
