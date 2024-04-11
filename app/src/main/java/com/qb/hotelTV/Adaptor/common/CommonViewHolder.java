package com.qb.hotelTV.Adaptor.common;

import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;



/**
 * 通用adapter.viewHolder
 *
 * @author cmt
 */
public class CommonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    //此处用sparse来储存比hashmap更省内存，减少findByViewId的次数
    private SparseArray<View> viewSparseArray;
    //item的点击事件
    private OnCommonItemEventListener commonItemEventListener;
    private int viewType;

    public CommonViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        viewSparseArray = new SparseArray<>();
    }
    public CommonViewHolder(View itemView, int viewType) {
        super(itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
        viewSparseArray = new SparseArray<>();
        this.viewType = viewType;
    }

    // 将该视图变成加载更多的视图
//    public void initLoadMore(){
//        // 显示加载更多
//        itemView.findViewById(R.id.show_more).setVisibility(View.VISIBLE);
//    }
    public int getViewType() {
        return viewType;
    }
    // 将该视图变成加载更多的视图
//    public void initLastOne(){
//        // 显示加载更多
//        TextView textView = itemView.findViewById(R.id.show_more);
//        textView.setVisibility(View.VISIBLE);
//        textView.setText("Already the last one");
//    }

    /**
     * 根据id 来找view
     *
     * @param viewId
     * @param <T>
     * @return
     */
    public <T extends View> T getView(int viewId) {
        //现在缓存中找，找到就直接返回
        View view = viewSparseArray.get(viewId);
        if (view == null) {
            //没有找到的话在去findByView
            view = itemView.findViewById(viewId);
//            viewSparseArray.put(viewId, view);
        }
        return (T) view;
    }

    @Override
    public void onClick(View view) {
        if (commonItemEventListener != null) {
            commonItemEventListener.onItemClick(view, getAdapterPosition());
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (commonItemEventListener != null) {
            commonItemEventListener.onItemLongClick(view.getId(), getAdapterPosition());
        }
        return false;
    }

    //设置view的文本
    public CommonViewHolder setText(int viewId, CharSequence str) {
        TextView tv = getView(viewId);
        tv.setText(str);
        return this;
    }

    //设置view的可见性
    public CommonViewHolder setViewVisibility(int viewId, int mode) {
        getView(viewId).setVisibility(mode);
        return this;
    }

    //设置view的图片资源
    public CommonViewHolder setViewImageRes(int viewId, int resId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }

    //设置view的图片背景
//    public CommonViewHolder setViewImageBG(int viewId, int resId) {
//        ImageView imageView = getView(viewId);
//        Glide.with(MyApplication.getContext()).load(resId).into(imageView);
//        return this;
//    }
    public CommonViewHolder setBackground(int viewId, Drawable res){
        View view = getView(viewId);
        view.setBackground(res);
        return this;
    }

//    public CommonViewHolder setGlideViewImageBG(int viewId,String url){
//        ImageView imageView = getView(viewId);
//        Glide.with(MyApplication.getContext()).load(url).into(imageView);
//        return this;
//    }

    //item 的事件处理
    public interface OnCommonItemEventListener {
        void onItemClick(View view, int position);

        void onItemLongClick(int viewId, int position);
    }

    //设置item 事件监听函数，具体的逻辑处理，放在他的调用类去执行
    public void setCommonClickListener(OnCommonItemEventListener commonClickListener) {
        this.commonItemEventListener = commonClickListener;
    }



}
