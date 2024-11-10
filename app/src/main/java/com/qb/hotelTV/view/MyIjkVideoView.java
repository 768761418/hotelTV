package com.qb.hotelTV.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MyIjkVideoView extends FrameLayout {

    private Context mContext;//上下文
    private IjkMediaPlayer mMediaPlayer = null;//视屏播放
    private TextView subtitleDisplay;//字幕View
    private SurfaceView surfaceView;//视图主要适用于被动更新，例如频繁地刷新

    private VideoPlayerListener listener;//jiant

    /**
     * 视频文件地址
     */
    private String mPath = "";

    private boolean mEnableBackgroundPlay = false;//是否开启后台服务

    public MyIjkVideoView(@NonNull Context context) {
        super(context);
        initVideoView(context);
    }

    public MyIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public MyIjkVideoView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    /**
     * 初始化VideoView
     */
    private void initVideoView(Context context) {
        mContext = context;

        //是否开启后台播放，如果开启了后台播放，就启动一个Service来做后台播放。
        initBackground();
        //初始化渲染器，创建SurfaceView或TextureView，并addView()。（IjkVideoView是FrameLayout）
        initRenders();

        //焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        //字幕View
        subtitleDisplay = new TextView(context);
        subtitleDisplay.setTextSize(24);
        subtitleDisplay.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams layoutParams_txt = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM);
        addView(subtitleDisplay, layoutParams_txt);

    }

    private void initRenders() {

    }

    private void initBackground() {
        //这里相当于没有进去，具体需要再处理
        if (mEnableBackgroundPlay) {
            mMediaPlayer = new IjkMediaPlayer();
        }

    }

    /**
     * 设置视频地址。
     * 根据是否第一次播放视频，做不同的操作。
     *
     * @param path the path of the video.
     */
    public void setVideoPath(String path) {
        if (mPath.equals("")) {
            //如果是第一次播放视频，那就创建一个新的surfaceView
            mPath = path;
            createSurfaceView();
        } else {
            //否则就直接load
            mPath = path;
            load();
        }
    }

    /**
     * 新建一个SurfaceView
     */

    private void createSurfaceView() {
        //生成一个新的surface view
        surfaceView = new SurfaceView(mContext);
        surfaceView.getHolder().addCallback(new LmnSurfaceCallback());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT
                , LayoutParams.MATCH_PARENT, Gravity.CENTER);
        surfaceView.setLayoutParams(layoutParams);
        this.addView(surfaceView);
    }

    /**
     * surfaceView的监听器,内部类
     */
    private class LmnSurfaceCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(@NonNull SurfaceHolder holder) {
            //在SurfaceView创建
        }

        @Override
        public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
            //在SurfaceView改变,加载视屏
            load();

        }

        @Override
        public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            //在SurfaceView销毁

        }
    }

    /**
     * 加载视屏
     */
    private void load() {
        //每次都要重新创建IMediaPlayer
        createPlayer();

        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle-resolution-change", 1);
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);  // 开启音频增强
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1);  // 启用准确的定位
        mMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 10240 * 100);
        try {
            mMediaPlayer.setDataSource(mPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //给mediaPlayer设置视图
        mMediaPlayer.setDisplay(surfaceView.getHolder());
        //设置进行硬解码

        mMediaPlayer.prepareAsync();
    }

    private void createPlayer() {
        //去除旧的
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer.release();
        }

        //新建一个新的
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();
        ijkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        //开启硬解码        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mMediaPlayer = ijkMediaPlayer;


        if (listener != null) {
            mMediaPlayer.setOnPreparedListener(listener);
            mMediaPlayer.setOnErrorListener(listener);
            mMediaPlayer.setOnCompletionListener(listener);
        }
    }

    public void setListener(VideoPlayerListener listener) {
        this.listener = listener;
        if (mMediaPlayer != null) {
            mMediaPlayer.setOnPreparedListener(listener);
        }
    }


    public interface VideoPlayerListener extends IMediaPlayer.OnPreparedListener,
            IMediaPlayer.OnCompletionListener,
            IMediaPlayer.OnErrorListener {
    }

    /**
     * 下面配置一些暂停，开始，停止等方法
     */
    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }

    public void start() {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}

