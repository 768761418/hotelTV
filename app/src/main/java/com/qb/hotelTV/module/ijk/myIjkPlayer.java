package com.qb.hotelTV.module.ijk;

import android.content.Context;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import xyz.doikki.videoplayer.ijk.IjkPlayer;

public class myIjkPlayer extends IjkPlayer implements IMediaPlayer.OnErrorListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnPreparedListener,
        IMediaPlayer.OnVideoSizeChangedListener, IjkMediaPlayer.OnNativeInvokeListener {

    protected IjkMediaPlayer mMediaPlayer;


    public myIjkPlayer(Context context) {
        super(context);
    }

    @Override
    public void initPlayer() {
        super.initPlayer();

    }
}
