package com.qb.hotelTV.Adaptor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;

import java.util.ArrayList;

public class TvChannelAdaptor extends RecyclerView.Adapter<TvChannelAdaptor.TvChannelViewHolder> {

    Context context;
    ArrayList<VideoModel> videList;

    public TvChannelAdaptor(Context context, ArrayList<VideoModel> videList) {
        this.context = context;
        this.videList = videList;
    }

    @NonNull
    @Override
    public TvChannelAdaptor.TvChannelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.item_tv,parent,false);
        return new TvChannelAdaptor.TvChannelViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull TvChannelAdaptor.TvChannelViewHolder holder, int position) {
        holder.tv_name.setText(videList.get(position).getStreamName());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class TvChannelViewHolder extends RecyclerView.ViewHolder{

        TextView tv_name;
        public TvChannelViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }
}
