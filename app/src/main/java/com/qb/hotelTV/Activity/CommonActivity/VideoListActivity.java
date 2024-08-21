package com.qb.hotelTV.Activity.CommonActivity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.LayoutCommonVideoListBinding;

import java.util.ArrayList;


public class VideoListActivity extends BaseActivity {

    private LayoutCommonVideoListBinding layoutCommonVideoListBinding;
    private ArrayList<CmsMessageModel> cms = new ArrayList<>();

//    @Override
//    protected void onPause() {
//        super.onPause();
//        layoutCommonVideoListBinding.playerView.pause();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        layoutCommonVideoListBinding.playerView.resume();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        layoutCommonVideoListBinding.playerView.release();
//    }


//    @Override
//    public void onBackPressed() {
//        if (!layoutCommonVideoListBinding.playerView.onBackPressed()) {
//            super.onBackPressed();
//        }
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        layoutCommonVideoListBinding = DataBindingUtil.setContentView(VideoListActivity.this, R.layout.layout_common_video_list);

    }





}

