package com.qb.hotelTV.Activity.Hotel;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.LayoutVideoBinding;

public class VideoActivity extends BaseActivity {
    LayoutVideoBinding layoutVideoBinding;
    private SimpleExoPlayer player ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    @Override
    protected void onDestroy() {
        if (player != null){
            player.release();
        }
        super.onDestroy();

    }

    private void init(){
        layoutVideoBinding = DataBindingUtil.setContentView(VideoActivity.this, R.layout.layout_video);
        String url = getIntent().getStringExtra("videoUrl");
        if (url != null && !url.equals("")){
            player = new SimpleExoPlayer.Builder(VideoActivity.this).build();
//                                绑定player
            layoutVideoBinding.video.setPlayer(player);
            // 隐藏控制面板
            layoutVideoBinding.video.setUseController(false);
//                                设置循环播放
//            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            MediaItem mediaItem = MediaItem.fromUri(url);
            player.setMediaItem(mediaItem);
            player.prepare();
            player.play();
        }

        String bg = getIntent().getStringExtra("bg");
        String title = getIntent().getStringExtra("title");
        if (bg != null) {
            Glide.with(VideoActivity.this)
                    .load(bg)
                    .error(R.drawable.app_bg)
                    .into(layoutVideoBinding.videoBackground);
        }
        if(title != null){
            layoutVideoBinding.videoTitle.setText(title);
        }
    }
}
