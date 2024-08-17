package com.qb.hotelTV.Activity.Theme;

import static com.qb.hotelTV.Utils.TimeUtil.getCurrentDateTime;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamgyf.android.ui.widget.textview.marquee.MarqueeTextView;
import com.qb.hotelTV.Activity.HomeActivity;
import com.qb.hotelTV.Data.CommonData;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.module.InputMessageDialog;

//主题的通用方法和配资
public class ThemeActivity extends HomeActivity {
    public String serverAddress,tenant,roomNumber;
    public SharedPreferencesUtils sharedPreferencesUtils;
    //    输入配置的dialog
    public InputMessageDialog inputMessageDialog;
    public Handler handler = new Handler();
    public Intent websocketService;


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
