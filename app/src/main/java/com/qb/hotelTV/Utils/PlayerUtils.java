package com.qb.hotelTV.Utils;

import android.content.Context;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.qb.hotelTV.huibuTv.FfmpegRenderersFactory;
import com.qb.hotelTV.view.MyIjkVideoView;






public class PlayerUtils {


    public static void startPlayer(Context context, MyIjkVideoView videoView, String url, boolean isLive){
        videoView.setVideoPath(url);
//        StandardVideoController controller = new StandardVideoController(context);
//        controller.addDefaultControlComponent("标题", isLive);
        videoView.start();

    }
}
