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

public class PlayerUtils {
    public Player getStartPlayer(Context context, String url){
      Player player = new ExoPlayer
                .Builder(context,new FfmpegRenderersFactory(context))
                .build();
//                                设置循环播放
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        return player;
    }

    public static void startPlayer(Context context,VideoView videoView,String url){
        videoView.setPlayerFactory(ExoMediaPlayerFactory.create());
        videoView.setUrl(url); //设置视频地址
        StandardVideoController controller = new StandardVideoController(context);
        controller.addDefaultControlComponent("标题", false);
        videoView.setVideoController(controller); //设置控制器
        videoView.start(); //开始播放，不调用则不自动播放
    }
}
