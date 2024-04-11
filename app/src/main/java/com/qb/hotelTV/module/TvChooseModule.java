package com.qb.hotelTV.module;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qb.hotelTV.Adaptor.TvChannelAdaptor;
import com.qb.hotelTV.R;

public class TvChooseModule extends Dialog {
    RecyclerView recyclerView;
    TvChannelAdaptor tvChannelAdaptor;
    public TvChooseModule(@NonNull Context context, TvChannelAdaptor tvChannelAdaptor) {
        super(context);
        this.tvChannelAdaptor = tvChannelAdaptor;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module_tv_choose);
        initUi();
    }

    private void initUi(){
        recyclerView = findViewById(R.id.tv_choose_recycler);
        recyclerView.setAdapter(tvChannelAdaptor);
        // 设置对话框的动画效果
        getWindow().setWindowAnimations(R.style.dialog_animation);
        // 设置对话框的宽度
        Window window = getWindow();
// 把 DecorView 的默认 padding 取消，同时 DecorView 的默认大小也会取消
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
// 设置宽度
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setGravity(Gravity.BOTTOM);
        window.setAttributes(layoutParams);
// 给 DecorView 设置背景颜色，很重要，不然导致 Dialog 内容显示不全，有一部分内容会充当 padding，上面例子有举出
        window.getDecorView().setBackgroundColor(Color.WHITE);
        window.setAttributes(layoutParams);

    }
}
