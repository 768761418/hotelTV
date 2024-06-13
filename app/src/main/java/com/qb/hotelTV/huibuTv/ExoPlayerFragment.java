package com.qb.hotelTV.huibuTv;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.leanback.app.BrowseSupportFragment;
import com.cdnbye.sdk.P2pEngine;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;


//    exo_player的fragment
public class ExoPlayerFragment extends androidx.fragment.app.Fragment implements BrowseSupportFragment.MainFragmentAdapterProvider {
    private static final String TAG = "ExoPlayerFragment";
    private static final String KEY_CONTENT = "ExoPlayerFragment:Content";
//    private Channel mContent = new Channel("CCTV-1", "http://183.63.15.42:9901/tsfile/live/0001_1.m3u8");
//    private Channel mContent = new Channel("CCTV-1", "http://play-flive.ifeng.com/live/06OLEEWQKN4.m3u8");
    private VideoModel mContent = new VideoModel("CCTV-1", "");
    private BrowseSupportFragment.MainFragmentAdapter mMainFragmentAdapter = new BrowseSupportFragment.MainFragmentAdapter(this);
    private ExoPlayer mExoPlayer;
    private int channelIndex = 0;
    public static ExoPlayerFragment newInstance(VideoModel content, int channelIndex) {
        ExoPlayerFragment fragment = new ExoPlayerFragment();
        fragment.mContent = content;
        fragment.channelIndex = channelIndex;
        return fragment;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//            if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
//                mContent = savedInstanceState.getString(KEY_CONTENT);
//            }
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(getContext())
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
        mExoPlayer = new ExoPlayer.Builder(getContext(),renderersFactory).build();
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.exo_player_fragment, container, false);
    }
    TextView full_screen;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        full_screen = view.findViewById(R.id.full_screen);
//            设置为频道名称
        full_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                full_screen.setVisibility(View.GONE);
//                Toast.makeText(getActivity(),"点击全屏",Toast.LENGTH_SHORT).show();
            }
        });
        StyledPlayerView playerView = view.findViewById(R.id.player_view);
        playerView.setPlayer(mExoPlayer);
        Log.d(TAG, "onViewCreated: " + mContent.getStreamUrl() + "///" + mContent.getStreamName());

        playVideo(mContent.getStreamUrl());

        getMainFragmentAdapter().getFragmentHost().notifyViewCreated(mMainFragmentAdapter);
//        log
//        System.out.println("ExoPlayerFragment onViewCreated");
    }
    private void playVideo(String url) {
        // Build the media item.
//        String parsedUrl = P2pEngine.getInstance().parseStreamUrl(url);
        String parsedUrl = url;

        MediaItem mediaItem = MediaItem.fromUri(parsedUrl);
        // Set the media item to be played.
        if(mExoPlayer!=null&&mExoPlayer.isPlaying()){
            mExoPlayer.stop();
        }
        mExoPlayer.setMediaItem(mediaItem);
        // Prepare the player.
        mExoPlayer.prepare();
//            设置声音为最大
        mExoPlayer.setVolume(1f);
        // Start the playback.
        mExoPlayer.play();
//        设置channelIndex
        MyApplication.setChannelIndex(channelIndex);
    }
    @Override
    public void onPause() {
        super.onPause();
        if(mExoPlayer!=null){
            mExoPlayer.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mExoPlayer != null) {
            mExoPlayer.prepare();
            mExoPlayer.play();
        }
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

}
