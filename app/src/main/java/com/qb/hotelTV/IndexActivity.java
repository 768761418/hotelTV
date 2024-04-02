package com.qb.hotelTV;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.os.Handler;

import com.qb.hotelTV.databinding.LayoutIndexBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class IndexActivity extends AppCompatActivity {
    private Handler handler = new Handler();
    LayoutIndexBinding layoutIndexBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }
    private void initUI(){
        layoutIndexBinding = DataBindingUtil.setContentView(this,R.layout.layout_index);
        startUpdateTask();
    }



    private void startUpdateTask() {
        // 创建一个新的 Runnable 对象，用于更新日期和时间
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
                layoutIndexBinding.IndexDate.setText(datePart);
                layoutIndexBinding.IndexTime.setText(timePart);

                // 间隔一段时间后再次执行任务（这里设置为每秒更新一次）
                handler.postDelayed(this, 1000);
            }
        };

        // 执行第一次任务
        handler.post(updateTask);
    }
    private String getCurrentDateTime() {
        // 获取当前日期和时间，并格式化为字符串
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    @Override
    protected void onDestroy() {
        // 移除所有未执行的任务，避免内存泄漏
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}