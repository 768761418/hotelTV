package com.qb.hotelTV.Activity.CommonActivity;

import android.database.DatabaseUtils;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.PlayerUtils;
import com.qb.hotelTV.databinding.LayoutVideoPlayerBinding;


public class VideoPlayerActivity extends AppActivity{

    private String url;
    private LayoutVideoPlayerBinding layoutVideoPlayerBinding;

    @Override
    protected void onPause() {
        super.onPause();
        layoutVideoPlayerBinding.xyzPlayer.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        layoutVideoPlayerBinding.xyzPlayer.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        layoutVideoPlayerBinding.xyzPlayer.release();
    }


    @Override
    public void onBackPressed() {
        if (!layoutVideoPlayerBinding.xyzPlayer.onBackPressed()) {
            super.onBackPressed();
        }
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutVideoPlayerBinding = DataBindingUtil.setContentView(VideoPlayerActivity.this,R.layout.layout_video_player);
        url = getIntent().getStringExtra("url");
        PlayerUtils.startPlayer(this,layoutVideoPlayerBinding.xyzPlayer,url,false);

    }


}
