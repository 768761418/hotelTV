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
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.huibuTv.utils.GetMAC;

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
    private static final String KEY_SERVER_ADDRESS = "server_address";
    private static final String KEY_ROOM_NUMBER = "room_number";
    private static final String KEY_TENANT = "tenant";
    private static final String PREFS_NAME = "HotelTV";
    SharedPreferences sharedPreferences;
    FocusScaleListener focusScaleListener = new FocusScaleListener();
    static Context mContext;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupUi();
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
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        prepareEntranceTransition();
    }

    private void loadData() {
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                createRows();
                startEntranceTransition();
            }
        }, 2000);

    }



    //    private void loadChannelFromServer(){
//        sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
//        String serverAddress = sharedPreferences.getString(KEY_SERVER_ADDRESS, "");
//        String tenant  = sharedPreferences.getString(KEY_TENANT,"");
//        BackstageHttp.getInstance().getTvChannel(serverAddress, tenant, new BackstageHttp.TvChannelCallback() {
//            @Override
//            public void onTvChannelResponse(ArrayList<VideoModel> videoModels) {
//                ArrayList<Channel> channelsList = MyApplication.getChannelsList();
//                Log.d("TAG", "createRows: "+channelsList.size());
//                for (int i = 0; i < channelsList.size(); i++) {
//                    Channel channel = channelsList.get(i);
//                    mRowsAdapter.add(new PageRow(new HeaderItem(i, (i+1)+" "+channel.getName())));
//                }
//            }
//
//            @Override
//            public void onTvChannelFailure(int code, String msg) {
//
//            }
//        });
//    }

    private void createRows() {
//        获取本机mac地址
        String mac = GetMAC.getDeviceMacAddress();
        Log.d("mac", mac);
//      向服务器验证mac地址
//        String status= GetMAC.checkMacAddress(mac);
        String status = "2";
//        结果0：不存在，1：存在但过期，2：可用
        switch (status){
            case "0":
                String message="您的设备未注册，请联系管理员并提供MAC地址\n"+mac;
                AlertDialog dialog = new AlertDialog.Builder(getActivity(), com.google.android.material.R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                        .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                        .setTitle(getString(R.string.app_name))//设置对话框的标题
                        .setMessage(message)//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                重试加载
                                dialog.dismiss();
                                createRows();
                            }
                        }).create();
                dialog.show();
                break;
            case "1":
                String message2="您的设备已过期，请联系管理员并提供MAC地址\n"+mac;
                AlertDialog dialog2 = new AlertDialog.Builder(getActivity(), com.google.android.material.R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                        .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                        .setTitle(getString(R.string.app_name))//设置对话框的标题
                        .setMessage(message2)//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                重试加载
                                dialog.dismiss();
                                createRows();
                            }
                        }).create();
                dialog2.show();
                break;
            case "2":
//                if(AutoUpdate.checkUpdate()){
//                    AutoUpdate.showUpdateDialog(getActivity());
//                }
                boolean addThing = true;
                if(addThing){
//                    ArrayList<Channel> channelsList = MyApplication.getChannelsList();
                    Context context = getContext();
                    ArrayList<VideoModel> channelsList = MyApplication.getVideoList(context);
                    for (int i = 0; i < channelsList.size(); i++) {
//                        Channel channel = channelsList.get(i);
                        VideoModel channel = channelsList.get(i);
                        mRowsAdapter.add(new PageRow(new HeaderItem(i, (i+1)+" "+channel.getStreamName())));
                    }
//                    授权
                    authorized = true;
                }
                else {
                    String message3="获取频道列表失败，请检查网络连接";
                    AlertDialog dialog3 = new AlertDialog.Builder(getActivity(), com.google.android.material.R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                            .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                            .setTitle(getString(R.string.app_name))//设置对话框的标题
                            .setMessage(message3)//设置对话框的内容
                            //设置对话框的按钮
                            .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().finish();
                                }
                            })
                            .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
//                                重试加载
                                    dialog.dismiss();
                                    createRows();
                                }
                            }).create();
                    dialog3.show();
                }

                break;
            case "3":
                String message3="服务器连接失败，请检查网络连接";
                AlertDialog dialog3 = new AlertDialog.Builder(getActivity(), com.google.android.material.R.style.Theme_AppCompat_DayNight_Dialog_Alert)
                        .setIcon(R.mipmap.ic_launcher)//设置标题的图片
                        .setTitle(getString(R.string.app_name))//设置对话框的标题
                        .setMessage(message3)//设置对话框的内容
                        //设置对话框的按钮
                        .setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                            }
                        })
                        .setPositiveButton("重试", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                重试加载
                                dialog.dismiss();
                                createRows();
                            }
                        }).create();
                dialog3.show();
                break;
        }
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
//                Channel channel = MyApplication.getChannelsList().get((int) row.getHeaderItem().getId());
                VideoModel channel = MyApplication.getVideoList(mContext).get((int) row.getHeaderItem().getId());
                Log.d(TAG, "createFragment: " + channel);
//                return new WebViewFragment();
                ExoPlayerFragment exoPlayerFragment = ExoPlayerFragment.newInstance(channel,(int) row.getHeaderItem().getId());
                return (androidx.fragment.app.Fragment) exoPlayerFragment;
//                return new GridFragment();
            }
            throw new IllegalArgumentException(String.format("Invalid row %s", rowObj));
        }
    }
    @Override
    public void onResume() {
        super.onResume();
    }


    public boolean isAuthorized() {
        return authorized;
    }
    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }


//    切换频道，true为下一个，false为上一个，循环
    public void toggleChannel(boolean next) {
        if (next) {
//            Log.d(TAG, "toggleChannel: " + getAdapter().size() + (getSelectedPosition() + 1));
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

}
