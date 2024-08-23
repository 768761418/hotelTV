package com.qb.hotelTV.Utils;

import android.content.Context;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.qb.hotelTV.huibuTv.FfmpegRenderersFactory;

import xyz.doikki.videocontroller.StandardVideoController;
import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
import xyz.doikki.videoplayer.player.VideoView;


//import xyz.doikki.videocontroller.StandardVideoController;
//import xyz.doikki.videoplayer.exo.ExoMediaPlayerFactory;
//import xyz.doikki.videoplayer.player.VideoView;

public class PlayerUtils {


    public static void startPlayer(Context context, VideoView videoView, String url, boolean isLive){
        videoView.setPlayerFactory(ExoMediaPlayerFactory.create());
        videoView.setUrl(url); //设置视频地址
        StandardVideoController controller = new StandardVideoController(context);
        controller.addDefaultControlComponent("标题", isLive);
        videoView.setVideoController(controller); //设置控制器
        videoView.start(); //开始播放，不调用则不自动播放
    }
}
