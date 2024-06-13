package com.qb.hotelTV.Activity.Hospital.fgm;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.qb.hotelTV.Activity.VideoActivity;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.FragmentVideoBinding;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentVideoBinding fragmentVideoBinding;
    private SimpleExoPlayer player ;
    private ArrayList<CmsMessageModel> cms = new ArrayList<>();
    private final String TAG = "VideoFragment";

    public VideoFragment() {
        // Required empty public constructor
    }
    @Override
    public void onDestroy() {
        if (player != null){
            player.release();
        }
        super.onDestroy();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentVideoBinding = fragmentVideoBinding.inflate(inflater,container,false);
        initUI();

        return fragmentVideoBinding.getRoot();
    }

    private void initUI(){
        String serverAddress = getActivity().getIntent().getStringExtra("serverAddress");
        String tenant = getActivity().getIntent().getStringExtra("tenant");

        int id = getActivity().getIntent().getIntExtra("id",-1);
        if (id != -1){
            BackstageHttp.getInstance().getCmsMessage(serverAddress, tenant, id, new BackstageHttp.CmsMessageCallBack() {
                @Override
                public void onCmsMessageResponse(ArrayList<CmsMessageModel> cmsMessageModels) {
                    cms.clear();
                    cms.addAll(cmsMessageModels);
                    String url = cms.get(0).getContent();
                    Log.d(TAG, "onCmsMessageResponse: ");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (url != null && !url.equals("")){
                                player = new SimpleExoPlayer.Builder(getContext()).build();
//                                绑定player
                                fragmentVideoBinding.video.setPlayer(player);
                                // 隐藏控制面板
                                fragmentVideoBinding.video.setUseController(false);
//                                设置循环播放
//            player.setRepeatMode(Player.REPEAT_MODE_ALL);
                                MediaItem mediaItem = MediaItem.fromUri(url);
                                player.setMediaItem(mediaItem);
                                player.prepare();
                                player.play();
                            }
                        }
                    });
                }

                @Override
                public void onCmsMessageFailure(int code, String msg) {

                }
            });
        }

    }
}