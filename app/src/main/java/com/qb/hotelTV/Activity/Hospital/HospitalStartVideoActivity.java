package com.qb.hotelTV.Activity.Hospital;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.R;

import java.util.EventListener;

public class HospitalStartVideoActivity extends BaseActivity {
    private SimpleExoPlayer player ;
    private PlayerView playerView;
    private ImageView imageView,nextBtn;
    private String url = "http://113.106.109.130:23000/admin-api/infra/file/24/get/19d44e6ac479abcc0a3561a0143584e955ee4584ac6daa3ba4844ebf5b974801.mp4";
    private String imgUrl = "http://113.106.109.130:23000/admin-api/infra/file/24/get/830b63674f2cbf5406db6f5a72131d0367c22f4c544aba7f60381b731ee73fc2.png";
    private Handler handler = new Handler();
    private boolean isVideo = true;
    private  String TAG = "HospitalStartVideoActivity";


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
        onClickNextBtn();
//        如果是视频的话
        if (isVideo){
            videoStart();
        } else {
            imgStart();
        }
    }

//    图片展示10s后进入下一页
    private void startActivityAfterDelay() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(HospitalStartVideoActivity.this, HospitalActivity.class);
                startActivity(intent);
                finish();
            }
        }, 10000); // 延迟10秒（10000毫秒）
    }

//    十秒之后出现一个按钮进入下一页
    private void showNextBtn(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                nextBtn.setVisibility(View.VISIBLE);
                nextBtn.clearFocus();
                nextBtn.requestFocus();

                nextBtn.setOnFocusChangeListener(focusScaleListener);
               Log.d(TAG, "run:111 ");
            }
        }, 3000); // 延迟10秒（10000毫秒）
    }

    private void onClickNextBtn(){
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HospitalStartVideoActivity.this, HospitalActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

//    视频启动方法
    private void videoStart(){
        imageView.setVisibility(View.GONE);
        playerView.setVisibility(View.INVISIBLE);
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
                    Intent intent = new Intent(HospitalStartVideoActivity.this, HospitalActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


//    图片启动方法
    private void imgStart(){
        imageView.setVisibility(View.INVISIBLE);
        playerView.setVisibility(View.GONE);
        Glide.with(HospitalStartVideoActivity.this)
                .load(imgUrl)
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
