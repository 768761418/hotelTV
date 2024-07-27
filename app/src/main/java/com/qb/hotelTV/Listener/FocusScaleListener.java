package com.qb.hotelTV.Listener;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

public class FocusScaleListener implements View.OnFocusChangeListener {
    private boolean isBorder;

    public void needBorder(boolean isBorder){
        this.isBorder = isBorder;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            v.animate().scaleX(1.03f).scaleY(1.03f).setDuration(300).start();
            if (isBorder){
                // 添加边框
                v.setBackground(getBorderDrawable());
            }

        } else {
            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
//            如果需要边框则移除边框
            if (isBorder){
                v.setBackground(null);
            }
            // 移除边框

        }
    }





    private Drawable getBorderDrawable() {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(3, Color.parseColor("#3f72af")); // 设置边框颜色和宽度
            return drawable;
        }
}