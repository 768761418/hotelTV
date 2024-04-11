package com.qb.hotelTV.Adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.module.TvChooseModule;

import java.util.ArrayList;

public class TvChannelAdaptor extends RecyclerView.Adapter<TvChannelAdaptor.TvChannelViewHolder> {

    Context context;
    ArrayList<VideoModel> videList;
    private TvChooseModule.SwitchListener switchListener;

    public TvChannelAdaptor(Context context, ArrayList<VideoModel> videList, TvChooseModule.SwitchListener switchListener) {
        this.context = context;
        this.videList = videList;
        this.switchListener = switchListener;
    }

    @NonNull
    @Override
    public TvChannelAdaptor.TvChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.item_tv,parent,false);
        return new TvChannelAdaptor.TvChannelViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TvChannelAdaptor.TvChannelViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_name.setText(videList.get(position).getStreamName());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(switchListener!=null){
                    switchListener.onSwitch(videList.get(position).getStreamUrl());
                }
                ;

            }
        });

    }

    @Override
    public int getItemCount() {
        return videList.size();
    }

    public static class TvChannelViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name;
        LinearLayout layout;
        public TvChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            layout = itemView.findViewById(R.id.tv_item_view);

        }
    }
}
