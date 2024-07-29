package com.qb.hotelTV.Listener;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

public class FocusScaleListener implements View.OnFocusChangeListener {
    private boolean isBorder;
    private Context context;

    public FocusScaleListener(Context context) {
        this.context = context;
    }

    public void needBorder(boolean isBorder){
        this.isBorder = isBorder;
    }

//    @Override
//    public void onFocusChange(View v, boolean hasFocus) {
//        if (hasFocus) {
//            v.animate().scaleX(1.03f).scaleY(1.03f).setDuration(300).start();
//            if (isBorder){
//                // 添加边框
//                v.setBackground(getBorderDrawable());
//            }
//
//        } else {
//            v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
////            如果需要边框则移除边框
//            if (isBorder){
//                v.setBackground(null);
//            }
//            // 移除边框
//
//        }
//    }
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                v.animate().scaleX(1.03f).scaleY(1.03f).setDuration(300).start();
                if (isBorder) {
                    // 添加边框
                    Drawable originalDrawable = v.getBackground();
                    if (originalDrawable == null) {
                        // 如果没有背景，设置一个透明的背景
                        originalDrawable = new ColorDrawable(Color.TRANSPARENT);
                    }
                    v.setTag(originalDrawable); // 保存原始背景
                    v.setBackground(getBorderDrawable(originalDrawable));
                }
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                if (isBorder) {
                    // 移除边框并恢复原始背景
                    Drawable originalDrawable = (Drawable) v.getTag(); // 获取原始背景
                    v.setBackground(originalDrawable);
                }
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