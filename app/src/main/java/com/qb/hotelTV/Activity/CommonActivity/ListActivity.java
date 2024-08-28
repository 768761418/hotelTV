package com.qb.hotelTV.Activity.CommonActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Data.ThemeType;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.LayoutHospitalListBinding;
import com.qb.hotelTV.module.hospital.BottomBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListActivity extends BaseActivity {
    private String serverAddress,tenant,roomNumber,title;
    private Long id;
    private LayoutHospitalListBinding layoutHospitalListBinding;
    private final String TAG = "ListActivity";
    private ArrayList<CmsMessageModel> cms = new ArrayList<>();
    private int currentPageNo = 1 ;
    private int type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    private void initUI(){
        layoutHospitalListBinding = DataBindingUtil.setContentView(this, R.layout.layout_hospital_list);
        String[] data = commonData.getData();
        serverAddress = data[0];
        tenant =data[1];
        roomNumber = data[2];

        id = getIntent().getLongExtra("id",-1);
        title = getIntent().getStringExtra("title");
        type = getIntent().getIntExtra("type",0);
        layoutHospitalListBinding.hospitalTop.setRoomNumber(roomNumber);
        //      获取天气和地址
        getGeoAndWeather(null,layoutHospitalListBinding.hospitalTop.weather());

        //        获取接口数据
        try {
            getData();
        }catch (Exception e){
            Log.e(TAG, "initUI: ", e);
        }
//        设置底部标题栏
        layoutHospitalListBinding.bottomBar.setTitle(title);


//        隐藏上一页初始化
        layoutHospitalListBinding.bottomBar.showUpAndNext();
        layoutHospitalListBinding.bottomBar.hideUpOrNext(true);
        getImageData();

        nextAndUpClick();

    }

    private void nextAndUpClick(){
        layoutHospitalListBinding.bottomBar.setNextOnclickListener(new BottomBar.NextOnclickListener() {
            @Override
            public void onNextOnclickListener() {
                currentPageNo+=1;
                layoutHospitalListBinding.bottomBar.showUpAndNext();
                Log.d(TAG, "onNextOnclickListener: " +currentPageNo);
                getImageData();
            }
        });
        layoutHospitalListBinding.bottomBar.setUpOnclickListener(new BottomBar.UpOnclickListener() {
            @Override
            public void onUpOnclickListener() {
                currentPageNo-=1;
                layoutHospitalListBinding.bottomBar.showUpAndNext();
                if (currentPageNo<=1){
                    layoutHospitalListBinding.bottomBar.hideUpOrNext(true);
                }
                getImageData();
            }
        });
    }



    private void getData() throws JSONException {
        JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
        if (hotelMessageJson != null){
            String logoUrl = hotelMessageJson.getString("iconUrl");;
            String bgUrl = hotelMessageJson.getString("homepageBackground");
//                初始化背景和logo
            initLogoAndBackGround(ListActivity.this,layoutHospitalListBinding.hospitalTop.logo(),logoUrl,layoutHospitalListBinding.hospitalBackground,bgUrl);
        }
    }




    private void getImageData(){

        BackstageHttp.getInstance().getCmsMessage(serverAddress, tenant, id, currentPageNo, new BackstageHttp.CmsMessageCallBack() {
            @Override
            public void onCmsMessageResponse(ArrayList<CmsMessageModel> cmsMessageModels) {
                cms.clear();
                cms.addAll(cmsMessageModels);
                Log.d(TAG, "onCmsMessageResponse: " + cms);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (cms.size()<6){
                            layoutHospitalListBinding.bottomBar.hideUpOrNext(false);
                            for (int i= cms.size();i<6;i++){
//                                LinearLayout item = (LinearLayout) ( (LinearLayout) layoutHospitalListBinding.hospitalList.getChildAt(i%2)).getChildAt(i % 3);
//                                ImageView imageView = (ImageView) item.getChildAt(0);
                                LinearLayout item = (LinearLayout)  layoutHospitalListBinding.hospitalList.getChildAt(i%2);
                                ImageView imageView = (ImageView) item.getChildAt(i % 3);
                                imageView.setVisibility(View.GONE);
                            }
                        }


                        for (int i = 0;i<cms.size();i++){
                            //获取到每一个item的layout替换掉图片和文字和跳转地址
                            LinearLayout item = (LinearLayout)  layoutHospitalListBinding.hospitalList.getChildAt(i%2);
                            ImageView imageView = (ImageView) item.getChildAt(i % 3);
//                            LinearLayout item = (LinearLayout) ( (LinearLayout) layoutHospitalListBinding.hospitalList.getChildAt(i%2)).getChildAt(i % 3);
//                            ImageView imageView = (ImageView) item.getChildAt(0);
                            imageView.setVisibility(View.VISIBLE);
                            imageView.setOnFocusChangeListener(focusScaleListener);
//                            加载图片
                            Glide.with(ListActivity.this)
                                    .load(cms.get(i).getPicUrl())
                                    .error(R.color.white)
                                    .into((imageView));


                            int finalI = i;
                            if (type == ThemeType.WEB_LIST){
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(ListActivity.this, WebActivity.class);
                                        String content = cms.get(finalI).getContent();
                                        Long imgId = cms.get(finalI).getCategoryId();
                                        intent.putExtra("serverAddress",serverAddress);
                                        intent.putExtra("tenant",tenant);
                                        intent.putExtra("roomNumber",roomNumber);
                                        intent.putExtra("title",title);
                                        intent.putExtra("id",imgId);
                                        intent.putExtra("content",content);

                                        startActivityForResult(intent,1);
                                    }
                                });
                            }else if (type == ThemeType.VIDEO_LIST){
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(ListActivity.this, VideoListActivity.class);
                                        String content = cms.get(finalI).getContent();
                                        Long imgId = cms.get(finalI).getCategoryId();
                                        intent.putExtra("serverAddress",serverAddress);
                                        intent.putExtra("tenant",tenant);
                                        intent.putExtra("roomNumber",roomNumber);
                                        intent.putExtra("title",title);
                                        intent.putExtra("id",imgId);
                                        intent.putExtra("content",content);
                                        intent.putExtra("cover",cms.get(finalI).getPicUrl());
                                        startActivityForResult(intent,1);
                                    }
                                });
                            }



                        }



                    }
                });
            }

            @Override
            public void onCmsMessageFailure(int code, String msg) {

            }
        });

    }

    // 处理从 WebActivity 返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: " +requestCode);
        Log.d(TAG, "onActivityResult: " + resultCode);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // 关闭 ListActivity
            finish();
        }
    }


}
