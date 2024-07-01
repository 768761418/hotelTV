package com.qb.hotelTV.module.hospital;

import static com.qb.hotelTV.Utils.TimeUtil.getCurrentDateTime;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.HospitalModuleTopBarBinding;

import org.w3c.dom.Text;

public class TopBar extends LinearLayout {
    private HospitalModuleTopBarBinding hospitalModuleTopBarBinding;
    private Handler handler = new Handler();
    private FocusScaleListener focusScaleListener = new FocusScaleListener();

    public TopBar(Context context) {
        super(context);
        initUI(context);

    }

    public TopBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
    }

    public TopBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context);
    }



    private void initUI(Context context){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        hospitalModuleTopBarBinding = DataBindingUtil.inflate(layoutInflater, R.layout.hospital_module_top_bar,this,true);
        startUpdateTask();

    }



    private void startUpdateTask() {
        Runnable updateTask = new Runnable() {
            @Override
            public void run() {
                // 获取当前日期和时间
                String currentDateTimeString = getCurrentDateTime(1);
                // 分割日期和时间
                String[] parts = currentDateTimeString.split(" ");
                String datePart = parts[0];
                String timePart = parts[1];
                // 更新 TextView 的文本内容为当前日期和时间的各部分
                hospitalModuleTopBarBinding.date.setText(datePart);
                hospitalModuleTopBarBinding.time.setText(timePart);

                // 间隔一段时间后再次执行任务（这里设置为每秒更新一次）
                handler.postDelayed(this, 1000);
            }
        };

        // 执行第一次任务
        handler.post(updateTask);
    }

    public void setLogo(String logo){
        Glide.with((Activity) getContext())
                .load(logo)
                .error(R.drawable.img)
                .into(hospitalModuleTopBarBinding.logo);
    }

    public void setRoomNumber(String roomNumber){
        hospitalModuleTopBarBinding.roomNumber.setText(roomNumber);
    }
    public void setWeather(String weather){
        hospitalModuleTopBarBinding.weather.setText(weather);
    }

    public void initOrUpdateTopBar(String logo,String roomNumber,String weather){
        setLogo(logo);
        setRoomNumber(roomNumber);
//        setWeather(weather);
    }

    public TextView weather(){
        return hospitalModuleTopBarBinding.weather;
    }
    public TextView roomNumber(){
        return hospitalModuleTopBarBinding.roomNumber;
    }
    public ImageView logo(){
        return hospitalModuleTopBarBinding.logo;
    }



}
