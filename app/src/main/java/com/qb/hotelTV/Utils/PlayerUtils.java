package com.qb.hotelTV.Utils;

import android.content.Context;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;

public class PlayerUtils {
    public Player getStartPlayer(Context context, String url){
        Player player = new SimpleExoPlayer.Builder(context).build();
//                                设置循环播放
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        MediaItem mediaItem = MediaItem.fromUri(url);
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();
        return player;
    }
}
