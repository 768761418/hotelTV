package com.qb.hotelTV.module.hospital;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.HospitalModuleBottomBarBinding;

public class BottomBar extends ConstraintLayout {
    private HospitalModuleBottomBarBinding hospitalModuleBottomBarBinding;
    private FocusScaleListener focusScaleListener = new FocusScaleListener();

    IndexOnclickListener indexOnclickListener;
    ComebackOnclickListener comebackOnclickListener;

    UpOnclickListener upOnclickListener;
    NextOnclickListener nextOnclickListener;

    public BottomBar(@NonNull Context context) {
        super(context);
        initUI(context);
    }

    public BottomBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initUI(context);
    }

    public BottomBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI(context);
    }

    public interface IndexOnclickListener{
        void onIndexOnclickListener();
    }
    public interface ComebackOnclickListener{
        void onComebackOnclickListener();
    }
    public interface UpOnclickListener{
        void onUpOnclickListener();
    }
    public interface NextOnclickListener{
        void onNextOnclickListener();
    }



    public void setIndexOnclickListener(IndexOnclickListener indexOnclickListener) {
        this.indexOnclickListener = indexOnclickListener;
    }
    public void setComebackOnclickListener(ComebackOnclickListener comebackOnclickListener){
        this.comebackOnclickListener = comebackOnclickListener;
    }

    public void setUpOnclickListener(UpOnclickListener upOnclickListener) {
        this.upOnclickListener = upOnclickListener;
    }
    public void setNextOnclickListener(NextOnclickListener nextOnclickListener){
        this.nextOnclickListener = nextOnclickListener;
    }



    private void initUI(Context context){
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        hospitalModuleBottomBarBinding = DataBindingUtil.inflate(layoutInflater, R.layout.hospital_module_bottom_bar,this,true);

        focusChange();
        hospitalModuleBottomBarBinding.hospitalIndex.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (indexOnclickListener != null){
                    indexOnclickListener.onIndexOnclickListener();
                }
                ((Activity)context).finish();
            }
        });
        hospitalModuleBottomBarBinding.hospitalComeback.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (comebackOnclickListener != null){
//                    comebackOnclickListener.onComebackOnclickListener();
//                }
                ((Activity)context).finish();
            }
        });

        hospitalModuleBottomBarBinding.hospitalUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (upOnclickListener!=null){
                    upOnclickListener.onUpOnclickListener();
                }

            }
        });

        hospitalModuleBottomBarBinding.hospitalNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nextOnclickListener!=null){
                    nextOnclickListener.onNextOnclickListener();
                }
            }
        });


    }

    private void focusChange(){
        hospitalModuleBottomBarBinding.hospitalUp.setOnFocusChangeListener(focusScaleListener);
        hospitalModuleBottomBarBinding.hospitalNext.setOnFocusChangeListener(focusScaleListener);
        hospitalModuleBottomBarBinding.hospitalComeback.setOnFocusChangeListener(focusScaleListener);
        hospitalModuleBottomBarBinding.hospitalIndex.setOnFocusChangeListener(focusScaleListener);
    }

    public void setTitle(String title){
        hospitalModuleBottomBarBinding.hospitalTitle.setText(title);
    }



//    隐藏翻页
    public void showUpAndNext(){
        hospitalModuleBottomBarBinding.hospitalUp.setVisibility(VISIBLE);
        hospitalModuleBottomBarBinding.hospitalNext.setVisibility(VISIBLE);
    }
//    展示翻页
    public void hideUpAndNext(){
        hospitalModuleBottomBarBinding.hospitalUp.setVisibility(INVISIBLE);
        hospitalModuleBottomBarBinding.hospitalNext.setVisibility(INVISIBLE);
    }

//    隐藏单个翻页
    public void hideUpOrNext(boolean isUp){
        if (isUp){
            hospitalModuleBottomBarBinding.hospitalUp.setVisibility(INVISIBLE);
        }else {
            hospitalModuleBottomBarBinding.hospitalNext.setVisibility(INVISIBLE);
        }
    }


    public int hospitalIndexId(){
        return R.id.hospital_index;
    }

    public int hospitalComebackId(){
        return R.id.hospital_comeback;
    }



}
