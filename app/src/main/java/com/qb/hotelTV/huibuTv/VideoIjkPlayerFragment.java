package com.qb.hotelTV.huibuTv;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.leanback.app.BrowseSupportFragment;

import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkPlayer;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.PlayerUtils;




public class VideoIjkPlayerFragment extends androidx.fragment.app.Fragment implements BrowseSupportFragment.MainFragmentAdapterProvider{
    private String TAG = "VideoIjkPlayerFragment";

    private BrowseSupportFragment.MainFragmentAdapter mMainFragmentAdapter = new BrowseSupportFragment.MainFragmentAdapter(this);
    private int channelIndex;
    private VideoModel videoModel;
    private IjkVideoView videoView;


    public static VideoIjkPlayerFragment newInstance(VideoModel videoModel, int channelIndex){
        VideoIjkPlayerFragment videoPlayerFragment = new VideoIjkPlayerFragment();
        videoPlayerFragment.channelIndex = channelIndex;
        videoPlayerFragment.videoModel = videoModel;
        return videoPlayerFragment;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.video_ijk_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        videoView = view.findViewById(R.id.player_view);

//        videoView.setEnableMediaCodec()
        playVideo(videoModel.getStreamUrl());
        getMainFragmentAdapter().getFragmentHost().notifyViewCreated(mMainFragmentAdapter);
    }

    private void playVideo(String url){
//        PlayerUtils.startPlayer(getContext(),videoView,url,true);

        videoView.setUrl(url); //设置视频地址
        videoView.setTitle("网易公开课-如何掌控你的自由时间"); //设置视频标题
        StandardVideoController controller = new StandardVideoController(getContext());
        videoView.setVideoController(controller); //设置控制器，如需定制可继承BaseVideoController
        PlayerConfig playerConfig = new PlayerConfig.Builder()
//                .enableCache() //启用边播边缓存功能
//                .autoRotate() //启用重力感应自动进入/退出全屏功能
                .enableMediaCodec()//启动硬解码，启用后可能导致视频黑屏，音画不同步
//                .usingSurfaceView() //启用SurfaceView显示视频，不调用默认使用TextureView
//                .savingProgress() //保存播放进度
//                .disableAudioFocus() //关闭AudioFocusChange监听
//                .setLooping() //循环播放当前正在播放的视频
                .build();
        videoView.setPlayerConfig(playerConfig);




        videoView.start(); //开始播放，不调用则不自动播放
        //        设置channelIndex
        MyApplication.setChannelIndex(channelIndex);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//            outState.putString(KEY_CONTENT, mContent);
    }

    @Override
    public BrowseSupportFragment.MainFragmentAdapter getMainFragmentAdapter() {
        return mMainFragmentAdapter;
    }



    @Override
    public void onPause() {
        super.onPause();
        videoView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.resume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.release();
    }









}
