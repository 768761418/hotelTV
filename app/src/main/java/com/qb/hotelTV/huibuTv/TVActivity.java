package com.qb.hotelTV.huibuTv;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.FragmentActivity;

import com.cdnbye.sdk.P2pEngine;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.qb.hotelTV.Activity.IndexActivity;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;

import java.util.ArrayList;
@Deprecated
public class TVActivity extends FragmentActivity {
    private static final String TAG = "TVActivity";
    private StyledPlayerView playerView;
    private ExoPlayer mExoPlayer;
    private int channelIndex = 0;
    private ContentLoadingProgressBar progressBar;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tvactivity);
//        获取频道名称与频道地址
        String channelName = getIntent().getStringExtra("channelName");
        String channelUrl = getIntent().getStringExtra("channelUrl");
//        在频道列表中查找频道序号
//        ArrayList<Channel> channels = MyApplication.getChannelsList();
        ArrayList<VideoModel> channels = MyApplication.getVideoList(this);
        for (int i = 0; i < channels.size(); i++) {
            if (channels.get(i).getStreamName().equals(channelName)) {
                channelIndex = i;
                break;
            }
        }

        MyApplication.setChannelIndex(channelIndex);
        playerView = findViewById(R.id.player_view_main);
        progressBar = findViewById(R.id.progressBar);
        mExoPlayer = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(mExoPlayer);
        Log.d(TAG, "onCreate: " + channelUrl);
        playVideo(channelUrl);
    }
    private void playVideo(String url) {
        if(url == null || url.isEmpty()) {
            return;
        }
        if(mExoPlayer.isPlaying()) {
            mExoPlayer.stop();
        }
        MediaItem mediaItem;

        String parsedUrl = P2pEngine.getInstance().parseStreamUrl(url);
        mediaItem = MediaItem.fromUri(parsedUrl);
        mExoPlayer.setMediaItem(mediaItem);
        mExoPlayer.prepare();
        mExoPlayer.play();
        progressBar.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mExoPlayer.isPlaying()) {
                    progressBar.hide();
                }else
                {//                    重新计时
                    new Handler().postDelayed(this, 1000);
                }
            }
        }, 1000);
    }
//    切换频道,true为向左切换,false为向右切换
    private void changeChannel(Boolean isLeft) {
//        获取频道列表
//        ArrayList<Channel> channels = MyApplication.getChannelsList();
        ArrayList<VideoModel> channels = MyApplication.getVideoList(this);
        if (isLeft) {
            channelIndex--;
            if (channelIndex < 0) {
                channelIndex = channels.size() - 1;
            }
        } else {
            channelIndex++;
            if (channelIndex >= channels.size()) {
                channelIndex = 0;
            }
        }
        playVideo(channels.get(channelIndex).getStreamUrl());
//        输出播放状态
        Log.e(TAG, "changeChannel: " + mExoPlayer.isLoading());
        MyApplication.setChannelIndex(channelIndex);
    }
//    按键事件
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                changeChannel(true);
                Log.d(TAG, "onKeyDown: 上");
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                changeChannel(false);
                Log.d(TAG, "onKeyDown: 下");
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                Log.d(TAG, "onKeyDown: 左");
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                Log.d(TAG, "onKeyDown: 右");
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
                Log.d(TAG, "onKeyDown: 确定");
//                执行返回键
                onBackPressed();
                break;
            case KeyEvent.KEYCODE_BACK:
                Log.d(TAG, "onKeyDown: 返回");
                break;
            case KeyEvent.KEYCODE_MENU:
                Log.d(TAG, "onKeyDown: 菜单");
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if(mExoPlayer != null) {
            mExoPlayer.release();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(RESULT_OK,intent);
        finish();
        super.onBackPressed();
    }
}