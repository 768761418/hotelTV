package com.qb.hotelTV.Activity;

import android.os.Bundle;

import androidx.annotation.Nullable;


import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.qb.hotelTV.R;

public class TestM3u8Activity extends BaseActivity{

    PlayerView playerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_m3u8);
        playerView = findViewById(R.id.test_player);
        String url = "http://121.205.4.89:88/hls/50/index.m3u8";
//        String url = "http://amdlive.ctnd.com.edgesuite.net/arirang_1ch/smil:arirang_1ch/master.m3u8";
        Player player = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        // 隐藏控制面板
        playerView.setUseController(false);
//        加载视频，视频加载完成播放
        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
    }
}
