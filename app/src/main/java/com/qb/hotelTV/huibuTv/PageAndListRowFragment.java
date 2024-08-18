/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.qb.hotelTV.huibuTv;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AlertDialog;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseFragment;
import androidx.leanback.app.BrowseSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.PageRow;
import androidx.leanback.widget.Row;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;

import java.util.ArrayList;

/**
 * Sample {@link BrowseFragment} implementation showcasing the use of {@link PageRow} and
 * {@link ListRow}.
 */
public class PageAndListRowFragment extends BrowseSupportFragment {
    private BackgroundManager mBackgroundManager;

    private ArrayObjectAdapter mRowsAdapter;
    private boolean authorized = false;
    private static final  String TAG = "PageAndListRowFragment";
    private static VLCPlayerFragment vlcPlayerFragment;

    static Context mContext;
    private ArrayList<VideoModel> channelsList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        加载header的函数
        setupUi();
//        加载header数据的函数
        loadData();

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());


        getMainFragmentRegistry().registerFragment(PageRow.class,
                new PageRowFragmentFactory(mBackgroundManager));

        mContext = getContext();
//        getView().requestFocus();
//        getView().setOnFocusChangeListener(focusScaleListener);
    }

    private void setupUi() {
//        启用header
        setHeadersState(HEADERS_ENABLED);
//        设置动画效果
        setHeadersTransitionOnBackEnabled(true);
//        设置header背景
        setBrandColor(getResources().getColor(R.color.fastlane_background));
//        入口过渡动画
        prepareEntranceTransition();
    }

    private void loadData() {

        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                创建header列表数据
                createRows();
//                过度动画
                startEntranceTransition();
            }
        }, 2000);

    }



    private void createRows() {
        Context context = getContext();
//        网络请求获的数据
        channelsList = MyApplication.getVideoList(context);
        for (int i = 0; i < channelsList.size(); i++) {
//                        Channel channel = channelsList.get(i);
            VideoModel channel = channelsList.get(i);
            mRowsAdapter.add(new PageRow(new HeaderItem(i, (i+1)+" "+channel.getStreamName())));
        }
//                    授权
        authorized = true;
    }




    private static class PageRowFragmentFactory extends FragmentFactory<androidx.fragment.app.Fragment> {
        private final BackgroundManager mBackgroundManager;

        PageRowFragmentFactory(BackgroundManager backgroundManager) {
            this.mBackgroundManager = backgroundManager;
        }

        @Override
        public androidx.fragment.app.Fragment createFragment(Object rowObj) {
            Row row = (Row) rowObj;
            mBackgroundManager.setDrawable(null);
            if (row.getHeaderItem().getId() < MyApplication.getVideoList(mContext).size()) {
//                根据ID获取对象
                VideoModel channel = MyApplication.getVideoList(mContext).get((int) row.getHeaderItem().getId());
//                return new WebViewFragment();
//                IJKPlayerFragment exoPlayerFragment = IJKPlayerFragment.newInstance(channel,(int) row.getHeaderItem().getId());
//                ExoPlayerFragment exoPlayerFragment = ExoPlayerFragment.newInstance(channel,(int) row.getHeaderItem().getId());
//                if (vlcPlayerFragment == null || !vlcPlayerFragment.isAdded()) {
//                    vlcPlayerFragment = VLCPlayerFragment.newInstance(channel,(int) row.getHeaderItem().getId());
//                } else {
//                    vlcPlayerFragment.playVideo(channel.getStreamUrl(),(int) row.getHeaderItem().getId());
//                }
                Log.d(TAG, "直播路径: " + channel.getStreamUrl());
                VideoPlayerFragment videoPlayerFragment = VideoPlayerFragment.newInstance(channel,(int) row.getHeaderItem().getId());
                TagPlayerFragment tagPlayerFragment = TagPlayerFragment.newInstance(channel,(int) row.getHeaderItem().getId());
//                VLCPlayerFragment vlcPlayerFragment = VLCPlayerFragment.newInstance(channel,(int) row.getHeaderItem().getId());
                return videoPlayerFragment;
            }
            throw new IllegalArgumentException(String.format("Invalid row %s", rowObj));
        }
    }


    public boolean isAuthorized() {
        return authorized;
    }


//    切换频道，true为下一个，false为上一个，循环
    public void toggleChannel(boolean next) {
        if (next) {
            if (getAdapter().size() != 0){
                setSelectedPosition((getSelectedPosition() + 1) % getAdapter().size());
            }else {
                setSelectedPosition((getSelectedPosition() + 1));
            }

        } else {
            setSelectedPosition((getSelectedPosition() - 1 + getAdapter().size()) % getAdapter().size());
        }
        getView().requestFocus();
    }

//    输入数字跳转到对应的台
    public void numberChangeChannel(int number){
        if (getAdapter().size() != 0 && getAdapter().size() >= number){
            setSelectedPosition(number-1);
        }
        getView().requestFocus();
    }

}
