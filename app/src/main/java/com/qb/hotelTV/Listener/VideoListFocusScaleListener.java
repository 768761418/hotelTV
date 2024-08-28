package com.qb.hotelTV.Listener;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.qb.hotelTV.R;

public class VideoListFocusScaleListener implements View.OnFocusChangeListener {
    private int type;
    private Context context;

    public VideoListFocusScaleListener(Context context,int type) {
        this.context = context;
        this.type = type;
    }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
        if (type == 1){
            if (hasFocus) {
                v.animate().scaleX(1.15f).scaleY(1.15f).setDuration(300).start();
//                itemBg.setBackgroundResource(R.drawable.item_three_select_bg);
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
//                itemBg.setBackgroundResource(R.drawable.item_three_bg);
            }
        }else if (type == 2){
            TextView item = v.findViewById(R.id.item_name);
            LinearLayout itemBg = v.findViewById(R.id.item_bg);
            if (hasFocus) {
//                v.animate().scaleX(1.15f).scaleY(1.15f).setDuration(300).start();
                itemBg.setBackgroundResource(R.drawable.bg_videolist_has_focu);
                item.setTextColor(Color.WHITE);
            } else {
//                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                itemBg.setBackgroundResource(R.drawable.bg_videolist_no_focu);
                item.setTextColor(ContextCompat.getColor(context,R.color.video_list_text));
            }
        }else {

        }


        }


}