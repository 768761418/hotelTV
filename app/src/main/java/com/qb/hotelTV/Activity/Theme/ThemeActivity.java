package com.qb.hotelTV.Activity.Theme;

import static com.qb.hotelTV.Utils.TimeUtil.getCurrentDateTime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.qb.hotelTV.Activity.CommonActivity.AppActivity;
import com.qb.hotelTV.Activity.CommonActivity.ListActivity;
import com.qb.hotelTV.Activity.CommonActivity.VideoActivity;
import com.qb.hotelTV.Activity.CommonActivity.WebActivity;
import com.qb.hotelTV.Activity.HomeActivity;
import com.qb.hotelTV.Data.CommonData;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Model.HotelListModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.huibuTv.MainActivity;
import com.qb.hotelTV.module.InputMessageDialog;

import java.util.ArrayList;

//主题的通用方法和配资
public class ThemeActivity extends HomeActivity {
    public String serverAddress,tenant,roomNumber;
    public SharedPreferencesUtils sharedPreferencesUtils;
    //    输入配置的dialog
    public InputMessageDialog inputMessageDialog;
    public Handler handler = new Handler();
    public Intent websocketService;

    private String TAG = "ThemeActivity";


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(websocketService);
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
                            case 10:
//                              电视界面
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                break;
                            case 9:
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
                            case 0:
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
                            case 1:
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
                            case 2:
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
                            case 3:
                                item.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        // TODO 视频列表
                                    }
                                });
                                break;
                            case 4:
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
//    private <T extends ViewGroup>  void startUpdateTvTextTask(Context context, MarqueeTextView marqueeTextView,T layout){
//        Runnable startUpdateTvTextTask = new Runnable() {
//            @Override
//            public void run() {
//                getAnnouncements(context,serverAddress, tenant,marqueeTextView);
//                boolean isLogin = CommonData.getInstance().getIsLogin();
//                if (!isLogin){
//                    sharedPreferencesUtils.clearData();
//                    showInputDialog(false);
//                    Toast.makeText(context,"该房间已被删除，请重新配置",Toast.LENGTH_SHORT).show();
//                    indexListUnableOnclick(layoutIndexBinding.apkLayout,4);
//
//                }else {
////                    initUI();
//                    handler.postDelayed(this, 30*1000);
//                }
//
//            }
//        };
//        handler.post(startUpdateTvTextTask);
//    }

}
