package com.qb.hotelTV.Listener;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

public class FocusScaleListener implements View.OnFocusChangeListener {

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(300).start();
            // 添加边框
//            v.setBackground(getBorderDrawable());
        } else {
            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
            // 移除边框
//            v.setBackground(null);
        }
    }



    private Drawable getBorderDrawable() {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(5, Color.parseColor("#3f72af")); // 设置边框颜色和宽度
            return drawable;
        }
}