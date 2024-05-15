package com.qb.hotelTV.Activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.LayoutHotelBinding;

public class HotelActivity extends BaseActivity{
    private LayoutHotelBinding layoutHotelBinding;
    private FocusScaleListener focusScaleListener = new FocusScaleListener();
    private String strHtml;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish(); // 结束当前活动
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        layoutHotelBinding = DataBindingUtil.setContentView(HotelActivity.this, R.layout.layout_hotel);
        strHtml = getIntent().getStringExtra("detail");
        if (strHtml != null && !strHtml.equals("")){
            layoutHotelBinding.hotelWeb.loadDataWithBaseURL(null,strHtml,"text/html", "UTF-8", null);
            layoutHotelBinding.hotelWeb.requestFocus();
            layoutHotelBinding.hotelWeb.setOnFocusChangeListener(focusScaleListener);
        }else {
            layoutHotelBinding.hotelWeb.setVisibility(View.GONE);
        }

        String bg = getIntent().getStringExtra("bg");
        if (bg != null) {
            Glide.with(HotelActivity.this)
                    .load(bg)
                    .error(R.drawable.app_bg)
                    .into(layoutHotelBinding.hotelBackground);
        }
    }
}
