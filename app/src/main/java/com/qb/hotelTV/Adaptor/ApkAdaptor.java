package com.qb.hotelTV.Adaptor;

import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.R;

import java.util.ArrayList;

public class ApkAdaptor extends RecyclerView.Adapter<ApkAdaptor.ApkViewHolder> {
    Context context;
    ArrayList<ApkModel> apkList;



    public ApkAdaptor( Context context, ArrayList<ApkModel> apkList) {
        this.context = context;
        this.apkList = apkList;
    }

    @NonNull
    @Override
    public ApkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(this.context).inflate(R.layout.item_apk,parent,false);
        return new ApkAdaptor.ApkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApkViewHolder holder, int position) {
//        holder.apk_logo.setImageBitmap();
        holder.apk_name.setText(apkList.get(position).getName());
        Glide.with(context)
                .load(apkList.get(position).getLogoUrl())
                .error(R.color.white)
                .into(holder.apk_logo);
        int bgColor = Color.parseColor(apkList.get(position).getBackgroundColor());
        holder.apk_item_view.setBackgroundColor(bgColor);
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
