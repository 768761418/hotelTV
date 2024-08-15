package com.qb.hotelTV.Adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.DownLoadUtil;


import java.util.ArrayList;

public class ApkAdaptor extends RecyclerView.Adapter<ApkAdaptor.ApkViewHolder> {
    Context context;
    ArrayList<ApkModel> apkList;
    FocusScaleListener focusScaleListener;
    private DownLoadUtil downloadUtil;



    public ApkAdaptor( Context context, ArrayList<ApkModel> apkList) {
        this.context = context;
        this.apkList = apkList;
        this.focusScaleListener = new FocusScaleListener(context);
        this.focusScaleListener.needBorder(true);
        downloadUtil = new DownLoadUtil(context);
    }

    @NonNull
    @Override
    public ApkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(this.context).inflate(R.layout.item_apk,parent,false);
        return new ApkAdaptor.ApkViewHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull ApkViewHolder holder, int position) {
//        holder.apk_logo.setImageBitmap();
        holder.apk_name.setText(apkList.get(position).getName());
        Glide.with(context)
                .load(apkList.get(position).getLogoUrl())
                .error(R.color.white)
                .into(holder.apk_logo);
        if(position == 0){
            holder.apk_item_view.requestFocus();
        }
        holder.apk_item_view.setOnFocusChangeListener(focusScaleListener);
        holder.apk_item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (apkList.get(position).getApkUrl() == null || apkList.get(position).getApkUrl().equals("")){
                    downloadUtil.gotoOtherApp(apkList.get(position).getSchemeUrl(),"");
                }else {
                    downloadUtil.gotoOtherApp(apkList.get(position).getSchemeUrl(),apkList.get(position).getApkUrl());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return apkList.size();
    }


    public static class ApkViewHolder extends RecyclerView.ViewHolder {
        TextView apk_name;
        ImageView apk_logo;
        LinearLayout apk_item_view;

        public ApkViewHolder(@NonNull View itemView) {
            super(itemView);
            apk_logo = itemView.findViewById(R.id.apk_logo);
            apk_name = itemView.findViewById(R.id.apk_name);
            apk_item_view = itemView.findViewById(R.id.apk_item_view);
        }
    }



}
