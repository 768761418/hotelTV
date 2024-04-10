package com.qb.hotelTV.Adaptor;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
    }

    @Override
    public int getItemCount() {
        return apkList.size();
    }


    public static class ApkViewHolder extends RecyclerView.ViewHolder {
        TextView apk_name;
        ImageView apk_logo;
        public ApkViewHolder(@NonNull View itemView) {
            super(itemView);
            apk_logo = itemView.findViewById(R.id.apk_logo);
            apk_name = itemView.findViewById(R.id.apk_name);
        }
    }
}
