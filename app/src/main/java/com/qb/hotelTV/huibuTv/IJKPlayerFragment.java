package com.qb.hotelTV.huibuTv;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.leanback.app.BrowseSupportFragment;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class IJKPlayerFragment extends androidx.fragment.app.Fragment implements BrowseSupportFragment.MainFragmentAdapterProvider {
    private static final String TAG = "IJKPlayerFragment";
    private static final String KEY_CONTENT = "IJKPlayerFragment:Content";
    private BrowseSupportFragment.MainFragmentAdapter mMainFragmentAdapter = new BrowseSupportFragment.MainFragmentAdapter(this);

    private VideoModel mContent = new VideoModel("CCTV-1", "");
    private int channelIndex = 0;
    private IjkMediaPlayer mIjkMediaPlayer;
    private SurfaceView mSurfaceView;

    public static IJKPlayerFragment newInstance(VideoModel content, int channelIndex) {
        IJKPlayerFragment fragment = new IJKPlayerFragment();
        fragment.mContent = content;
        fragment.channelIndex = channelIndex;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getContext())
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        // Initialize IjkMediaPlayer
        mIjkMediaPlayer = new IjkMediaPlayer();
        // Configure IjkMediaPlayer if necessary (e.g., set options)
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ijk_player_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView fullScreen = view.findViewById(R.id.full_screen);
        fullScreen.setOnClickListener(v -> {
            fullScreen.setVisibility(View.GONE);
            // Handle full screen click if needed
        });

        mSurfaceView = view.findViewById(R.id.player_view);
        mIjkMediaPlayer.setDisplay(mSurfaceView.getHolder());

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mIjkMediaPlayer.setDisplay(holder);
                Log.d(TAG, "surfaceCreated: ");
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
                Log.d(TAG, "surfaceChanged: ");
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
                Log.d(TAG, "surfaceDestroyed: ");
            }
        });

        playVideo(mContent.getStreamUrl());

        getMainFragmentAdapter().getFragmentHost().notifyViewCreated(mMainFragmentAdapter);
    }

    private void playVideo(String url) {
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1000000);
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "probesize", 1024000);
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec_all", 1);
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
        mIjkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");

        // 如果 IjkMediaPlayer 已经在播放，先停止它
        if (mIjkMediaPlayer != null && mIjkMediaPlayer.isPlaying()) {
            mIjkMediaPlayer.stop();
            mIjkMediaPlayer.reset();
        }

        // 设置数据源
        try {
            Log.d(TAG, "playVideo: " + url);
            mIjkMediaPlayer.setDataSource(url);
            Log.d(TAG, "playVideo: xxxs" );
            // 配置播放器的事件监听
            mIjkMediaPlayer.setOnPreparedListener(mp -> {
                // 播放准备好后开始播放
                mp.start();
                Log.d(TAG, "播放了: ");
                // 设置声音为最大
                mp.setVolume(1f, 1f);
            });

            mIjkMediaPlayer.setOnErrorListener((mp, what, extra) -> {
                // 处理错误
                Log.e(TAG, "播放错误: " + what + ", " + extra);
                return true;
            });

            mIjkMediaPlayer.setOnCompletionListener(mp -> {
                // 播放完成时的处理
                Log.d(TAG, "播放完成");
            });

            // 异步准备播放器
            mIjkMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 设置 channelIndex
        MyApplication.setChannelIndex(channelIndex);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (mIjkMediaPlayer != null && mIjkMediaPlayer.isPlaying()) {
            mIjkMediaPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.release();
            mIjkMediaPlayer = null;
        }
    }

    @Override
    public BrowseSupportFragment.MainFragmentAdapter getMainFragmentAdapter() {
        return mMainFragmentAdapter;
    }


}

