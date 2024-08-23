package com.qb.hotelTV.Activity.Theme;

import static com.qb.hotelTV.Utils.TimeUtil.getCurrentDateTime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.dreamgyf.android.ui.widget.textview.marquee.MarqueeTextView;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;
import com.qb.hotelTV.Activity.CommonActivity.AppActivity;
import com.qb.hotelTV.Activity.CommonActivity.ListActivity;
import com.qb.hotelTV.Activity.CommonActivity.VideoActivity;
import com.qb.hotelTV.Activity.CommonActivity.WebActivity;
import com.qb.hotelTV.Activity.HomeActivity;
import com.qb.hotelTV.Data.ThemeType;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Model.HotelListModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.DownLoadUtil;
import com.qb.hotelTV.Utils.PlayerUtils;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.huibuTv.MainActivity;
import com.qb.hotelTV.module.InputMessageDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

//主题的通用方法和配资
public class ThemeActivity extends HomeActivity {
    public String serverAddress,tenant,roomNumber;
    public SharedPreferencesUtils sharedPreferencesUtils;
    //    输入配置的dialog
    public InputMessageDialog inputMessageDialog;
    public Handler handler = new Handler();
    public Intent websocketService;
    public Player player;



    private String TAG = "ThemeActivity";


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(websocketService);
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int itemTV = getIntent().getIntExtra("itemTV",-1);
        if (itemTV != -1){
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("itemTV",itemTV);
            startActivity(intent);
        }
    }

    // 创建一个新的 Runnable 对象，用于更新日期和时间
    public void startUpdateTask(TextView date,TextView time) {
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                // 获取当前日期和时间
                String currentDateTimeString = getCurrentDateTime();
                // 分割日期和时间
                String[] parts = currentDateTimeString.split(" ");
                String datePart = parts[0];
                String timePart = parts[1];
                // 更新 TextView 的文本内容为当前日期和时间的各部分
                if (date != null){
                    date.setText(datePart);
                }
                if (time != null){
                    time.setText(timePart);
                }


                // 间隔一段时间后再次执行任务（这里设置为每秒更新一次）
                handler.postDelayed(this, 1000);
            }
        };

        // 执行第一次任务
        handler.post(updateTask);
    }



    //    界面点击模块事件
    public  <T extends ViewGroup> void indexListOnclick(Context context, T layout, ArrayList<HotelListModel> hotelListModels, boolean needBg, String serverAddress, String tenant, int max){
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < Math.min(hotelListModels.size(), max); i++) {
                        Log.d(TAG, i + "onApkResponse: " + hotelListModels.get(i).getName());
                        Log.d(TAG, i +"onApkResponse: " + hotelListModels.get(i).getBackgroundUrl());
                        Log.d(TAG, i +"onApkResponse: " + hotelListModels.get(i).getPicUrl());
                        Log.d(TAG, i +"onApkResponse: " + hotelListModels.get(i).getType());
                        //获取到每一个item的layout替换掉图片和文字和跳转地址
                        LinearLayout item = (LinearLayout)  layout.getChildAt(i);
                        item.setVisibility(View.VISIBLE);

                        ImageView img  = item.findViewById(R.id.item_img);

                        Glide.with((Activity) context)
                                .load(hotelListModels.get(i).getPicUrl())
                                .error(R.color.black)
                                .into(img);

                        ((TextView)item.findViewById(R.id.item_text)).setText(hotelListModels.get(i).getName());
                        item.setTouchscreenBlocksFocus(true);
                        item.setFocusable(true);

                        if (needBg){
                            SimpleTarget<Drawable> simpleTarget = new SimpleTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    item.setBackground(resource);
                                }
                            };
                            Glide.with((Activity) context)
                                    .load(hotelListModels.get(i).getBackgroundUrl())
                                    .error(R.color.white)
                                    .into(simpleTarget);
                        }

                        int finalI  = i;
                        switch (hotelListModels.get(i).getType()){
                            case ThemeType.TV:
//                              电视界面
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case ThemeType.APP_LIST:
//                              应用中心
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, AppActivity.class);
                                        intent.putExtra("title",hotelListModels.get(finalI).getName());
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case ThemeType.WEB:
//                              WEB图文
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, WebActivity.class);
                                        String title = hotelListModels.get(finalI).getName();
                                        Long id = hotelListModels.get(finalI).getId();
                                        defaultPutIntent(intent,title,id);
                                        Log.d(TAG, "onClick: " + title);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case ThemeType.WEB_LIST:
                                // 图文列表
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, ListActivity.class);
                                        String title = hotelListModels.get(finalI).getName();
                                        Long id = hotelListModels.get(finalI).getId();
                                        defaultPutIntent(intent,title,id);

                                        Log.d(TAG, "onClick: " + title);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case ThemeType.VIDEO:
//                              视频
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, VideoActivity.class);
                                        String title = hotelListModels.get(finalI).getName();
                                        Long id = hotelListModels.get(finalI).getId();
                                        defaultPutIntent(intent,title,id);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case ThemeType.VIDEO_LIST:
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // TODO 视频列表
                                    }
                                });
                                break;
                            case ThemeType.APP:
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String[] result = BackstageHttp.getInstance().getAppMessage(serverAddress,tenant,hotelListModels.get(finalI).getId());
                                                Log.d(TAG, "lanmu: " + result[0]);
                                                Log.d(TAG, "lanmu: " + result[1]);
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (result[1] == null || result[1].equals("")){
                                                            downLoadUtil.gotoOtherApp(result[0],"");
                                                        }else {
                                                            downLoadUtil.gotoOtherApp(result[0],result[1]);
                                                        }
                                                    }
                                                });


                                            }
                                        }).start();



                                    }
                                });
                                break;

                        }

                    }
                }
            });
        }catch (Exception e){

        }

    }

    public <T extends ViewGroup> void indexListUnableOnclick(T layout,int listNumber){
        for(int i= 0;i<listNumber;i++){
            //获取到每一个item的layout替换掉图片和文字和跳转地址
            LinearLayout item = (LinearLayout)  layout.getChildAt(i);
            item.setTouchscreenBlocksFocus(false);
            item.setFocusable(false);
        }

    }

    //    默认携带参数
    private void defaultPutIntent(Intent intent,String title,Long id){
        intent.putExtra("title",title);
        intent.putExtra("id",id);
    }
//


    //    初始化开机图片或视频
    public void initStartVideoOrImg(Context context, String serverAddress, String tenant,
                                    ImageView logoView, ImageView bgView, PlayerView playerView){
        try{
            downLoadUtil = new DownLoadUtil(context);
            JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
            if (hotelMessageJson != null){
                String logoUrl = hotelMessageJson.getString("iconUrl");;
                String bgUrl = hotelMessageJson.getString("homepageBackground");
                String videoUrl = hotelMessageJson.getString("resourceUrl");
                Log.d(TAG, "initStartVideoOrImg: " + videoUrl);
//                初始化背景和logo
                initLogoAndBackGround(context,logoView,logoUrl,bgView,bgUrl);
                downLoadUtil.inspectOrDownloadFile(videoUrl, new DownLoadUtil.FileDownloadCallback() {
                    @Override
                    public void onFileReady(String filePath) {
//                        标识下载完成
                        Log.d(TAG, "onFileReady: 初始化了视频");
                        initIndexVideo(playerView,filePath);

                    }
                });


            }

        }catch (JSONException e){
            Log.e(TAG, "initStartVideoOrImg: ", e);
        }
    }

    //    初始化首页视频
    public void initIndexVideo(PlayerView playerView, String url){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (playerView == null || url.equals("")){
                    if (playerView == null){
                        playerView.setVisibility(View.GONE);
                    }
                }else {
                    playerView.setVisibility(View.VISIBLE);
                    // 隐藏控制面板
                    playerView.setUseController(false);
                    //                                设置循环播放
                    player.setRepeatMode(Player.REPEAT_MODE_ALL);
                    MediaItem mediaItem = MediaItem.fromUri(url);
                    player.setMediaItem(mediaItem);
                    player.prepare();
                    player.play();
                    // 隐藏控制面板
                    playerView.setUseController(false);
                    playerView.setPlayer(player);
                }

            }
        });
    }

}
