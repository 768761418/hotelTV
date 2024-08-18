package com.qb.hotelTV.huibuTv;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.leanback.app.BrowseSupportFragment;

import com.mafanwei.vlcLibrary.VlcVideoLibrary;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;


public class TagPlayerFragment extends androidx.fragment.app.Fragment implements BrowseSupportFragment.MainFragmentAdapterProvider {
    private static final String TAG = "TagPlayerFragment";
    private VideoModel mContent;
    private BrowseSupportFragment.MainFragmentAdapter mMainFragmentAdapter = new BrowseSupportFragment.MainFragmentAdapter(this);
    private LibVLC mLibVLC;
    private MediaPlayer mMediaPlayer;
    private  Media media;
    private int channelIndex = 0;
    private int videoWidth ,videoHeight ;

    private VlcVideoLibrary.Builder builder;

    private EditText editText;

    public static TagPlayerFragment newInstance(VideoModel content, int channelIndex) {
        TagPlayerFragment fragment = new TagPlayerFragment();
        fragment.mContent = content;
        fragment.channelIndex = channelIndex;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        builder= new VlcVideoLibrary.Builder(getContext(),true);


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
        editText = view.findViewById(R.id.vlc_video_editText);

        fullScreen.setOnClickListener(v -> {
            fullScreen.setVisibility(View.GONE);
            // Handle full screen click if needed
        });

        SurfaceView videoLayout = view.findViewById(R.id.vlc_video_layout);
        videoLayout.setKeepScreenOn(true);
//设置一个要渲染的object: Surface/SurfaceView/TextureView/SurfaceTexture
        builder.setSurfaceView(videoLayout);

        playVideo(mContent.getStreamUrl(),channelIndex);

        getMainFragmentAdapter().getFragmentHost().notifyViewCreated(mMainFragmentAdapter);
    }

    private void releasePlayer() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }

            if (media != null) {
                media.release();
                media = null;
            }
        }
//            if (mMediaPlayer != null) {
//                mMediaPlayer.stop();
//                mMediaPlayer.getVLCVout().detachViews();
//                mMediaPlayer.release();
//                mMediaPlayer = null;
//            }

    }


    public void playVideo(String url,int index) {
///创建 VlcVideoLibrary对象
        VlcVideoLibrary vlcVideoLibrary = builder.create();
//设置一个播放url，比如 rtsp.
        vlcVideoLibrary.setPlayUrl(url);
//设置输出
        vlcVideoLibrary.setVlcVout();
//开始播放
        vlcVideoLibrary.startPlay();
        MyApplication.setChannelIndex(index);
    }


    @Override
    public BrowseSupportFragment.MainFragmentAdapter getMainFragmentAdapter() {
        return mMainFragmentAdapter;
    }

    public void showEdit(boolean isShow,String data){
        if (isShow){
            editText.setVisibility(View.VISIBLE);
            if (data != null){
                editText.setText(data);
            }
        }else {
            editText.setVisibility(View.GONE);
        }
    }
}

