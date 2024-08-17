package com.qb.hotelTV.Utils;

import android.content.Context;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.qb.hotelTV.huibuTv.FfmpegRenderersFactory;

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
}
