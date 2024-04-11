package com.qb.hotelTV.Adaptor.common;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;


import com.qb.hotelTV.R;

import java.util.List;


//适用于整个列表的都是同一种item类型的
public  abstract class CommonAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {
    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;
    private static final int ITEM_VIEW_TYPE_FOOTER =2;

    //用来加载item布局
    private LayoutInflater layoutInflater;

    //数据源
    private List<T> data;

    //item的 layout_id
    private int layoutId;


    //手动更新数据
    private void updateData(){
        notifyDataSetChanged();
    }

    public CommonAdapter(Context context, List<T> datalist, int layoutId){
        this.layoutId = layoutId;
        this.data =  datalist;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public CommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(layoutId,parent,false);
        view.setTag(R.id.aa,""+System.currentTimeMillis());
        return new CommonViewHolder(view);
    }


    @Override
    public void onBindViewHolder(CommonViewHolder holder, int position) {
        holder.setIsRecyclable(false);
//        if(getItemViewType(position)==ITEM_VIEW_TYPE_FOOTER){
//            holder.setText(R.id.get_more,"Loading...");
//        }else{
        bindData(holder,data.get(position),position);
//            bindData(holder,data.get(position),position);
//            Log.e("onBindViewHolder",""+position);
//        }
    }

    //绑定数据方法不在adapter里面实现,由子类来进行绑定操作
    public abstract void bindData(CommonViewHolder holder, T data,int position);

    @Override
    public int getItemCount() {
        return data.size();
    }


    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

}
