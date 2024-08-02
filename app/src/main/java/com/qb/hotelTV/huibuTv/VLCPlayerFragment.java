package com.qb.hotelTV.huibuTv;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.BrowseSupportFragment;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import java.util.ArrayList;


public class VLCPlayerFragment extends androidx.fragment.app.Fragment implements BrowseSupportFragment.MainFragmentAdapterProvider {
    private static final String TAG = "VLCPlayerFragment";
    private static final String KEY_CONTENT = "VLCPlayerFragment:Content";
    private VideoModel mContent;
    private BrowseSupportFragment.MainFragmentAdapter mMainFragmentAdapter = new BrowseSupportFragment.MainFragmentAdapter(this);
    private LibVLC mLibVLC;
    private MediaPlayer mMediaPlayer;
    private int channelIndex = 0;

    public static VLCPlayerFragment newInstance(VideoModel content, int channelIndex) {
        VLCPlayerFragment fragment = new VLCPlayerFragment();
        fragment.mContent = content;
        fragment.channelIndex = channelIndex;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        设置
        ArrayList<String> options = new ArrayList<>();
        options.add("--rtsp-tcp");//强制rtsp-tcp，加快加载视频速度

        options.add("--aout=opensles");

        options.add("--audio-time-stretch");

        //args.add("--sub-source=marq{marquee=\"%Y-%m-%d,%H:%M:%S\",position=10,color=0xFF0000,size=40}");//这行是可以再vlc窗口右下角添加当前时间的
        options.add("-vvv");



        mLibVLC = new LibVLC(getContext(),options);

        mMediaPlayer = new org.videolan.libvlc.MediaPlayer(mLibVLC);
        mMediaPlayer.setScale(0);//这行必须加，为了让视图填满布局
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.vlc_player_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView fullScreen = view.findViewById(R.id.full_screen);
        fullScreen.setOnClickListener(v -> {
            fullScreen.setVisibility(View.GONE);
            // Handle full screen click if needed
        });

        SurfaceView videoLayout = view.findViewById(R.id.vlc_video_layout);
        videoLayout.setKeepScreenOn(true);
        videoLayout.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
                //        mMediaPlayer.attachViews(videoLayout, null, false, true);
                mMediaPlayer.getVLCVout().setVideoView(videoLayout);
                mMediaPlayer.getVLCVout().attachViews();
//                获取surface尺寸，将视频自适应
                int videoWidth = videoLayout.getWidth();
                int videoHeight = videoLayout.getHeight();
                mMediaPlayer.getVLCVout().setWindowSize(videoWidth, videoHeight);
                // 设置视频的宽高比为填充模式，aspectRatio为null表示填充SurfaceView
                mMediaPlayer.setAspectRatio(videoWidth + ":" + videoHeight);
                mMediaPlayer.setScale(0); // 设置为0表示自动缩放以填充SurfaceView

                playVideo(mContent.getStreamUrl());
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
                mMediaPlayer.getVLCVout().detachViews();
            }
        });


        getMainFragmentAdapter().getFragmentHost().notifyViewCreated(mMainFragmentAdapter);
    }

    private void playVideo(String url) {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        Media media = new Media(mLibVLC, Uri.parse(url));
        int cache =10;

        media.addOption(":network-caching=" + cache);
        media.addOption(":file-caching=" + cache);
        media.addOption(":live-cacheing=" + cache);
        media.addOption(":sout-mux-caching=" + cache);
        media.addOption(":codec=mediacodec,iomx,all");

        mMediaPlayer.setMedia(media);
        mMediaPlayer.play();
        MyApplication.setChannelIndex(channelIndex);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.play();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mLibVLC.release();
    }

    @Override
    public BrowseSupportFragment.MainFragmentAdapter getMainFragmentAdapter() {
        return mMainFragmentAdapter;
    }
}

