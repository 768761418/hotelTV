package com.qb.hotelTV.Listener;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.qb.hotelTV.R;

public class HotelTwoFocusScaleListener implements View.OnFocusChangeListener {
    private boolean isBorder;
    private Context context;

    public HotelTwoFocusScaleListener(Context context) {
        this.context = context;
    }

    public void needBorder(boolean isBorder){
        this.isBorder = isBorder;
    }
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            ConstraintLayout itemBg = v.findViewById(R.id.item_bg);
            if (hasFocus) {
                itemBg.animate().scaleX(1.15f).scaleY(1.15f).setDuration(300).start();
                itemBg.setBackgroundResource(R.drawable.item_three_select_bg);
            } else {
                itemBg.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                itemBg.setBackgroundResource(R.drawable.item_three_bg);
            }
        }


    private Drawable getBorderDrawable() {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.RECTANGLE);
            drawable.setStroke(3, Color.parseColor("#3f72af")); // 设置边框颜色和宽度
            return drawable;
        }

    private Drawable getBorderDrawable(Drawable originalDrawable) {
        GradientDrawable borderDrawable = new GradientDrawable();
        borderDrawable.setShape(GradientDrawable.RECTANGLE);
        borderDrawable.setStroke(5, Color.parseColor("#3f72af")); // 设置边框颜色和宽度

        // 创建一个LayerDrawable
        LayerDrawable layerDrawable;
        if (originalDrawable instanceof LayerDrawable) {
            layerDrawable = (LayerDrawable) originalDrawable.mutate(); // 避免影响原始背景
        } else {
            Drawable[] layers = new Drawable[2];
            layers[0] = originalDrawable; // 原有背景
            layers[1] = borderDrawable; // 边框
            layerDrawable = new LayerDrawable(layers);
        }
        return layerDrawable;
//        GradientDrawable borderDrawable = new GradientDrawable();
//        borderDrawable.setShape(GradientDrawable.RECTANGLE);
//        borderDrawable.setStroke(5, Color.parseColor("#3f72af")); // 设置边框颜色和宽度
//
//        Drawable[] layers = new Drawable[2];
//        layers[0] = originalDrawable; // 原有背景
//        layers[1] = borderDrawable; // 边框
//
//        return new LayerDrawable(layers);
    }

}