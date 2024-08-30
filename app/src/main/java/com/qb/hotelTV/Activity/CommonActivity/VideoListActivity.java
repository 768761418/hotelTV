package com.qb.hotelTV.Activity.CommonActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Adaptor.common.CommonAdapter;
import com.qb.hotelTV.Adaptor.common.CommonViewHolder;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.VideoListFocusScaleListener;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.LayoutCommonVideoListBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class VideoListActivity extends BaseActivity {

    private LayoutCommonVideoListBinding layoutCommonVideoListBinding;
    private ArrayList<CmsMessageModel> cms = new ArrayList<>();
    private String serverAddress,tenant,roomNumber,title,cover;
    private Long id;
    private String TAG = "VideoListActivity";
    private CommonAdapter<CmsMessageModel> commonAdapter;
    private  VideoListFocusScaleListener comeBackScaleListener,holderScaleListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        layoutCommonVideoListBinding = DataBindingUtil.setContentView(VideoListActivity.this, R.layout.layout_common_video_list);
//        获取基础信息
        String[] data = commonData.getData();
        serverAddress = data[0];
        tenant =data[1];
        roomNumber = data[2];

        id = getIntent().getLongExtra("id",-1);
        title = getIntent().getStringExtra("title");
        cover = getIntent().getStringExtra("cover");
//        strHtml = getIntent().getStringExtra("content");


        initUI();
    }

    private void initUI(){



        layoutCommonVideoListBinding.videoListComeback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        comeBackScaleListener = new VideoListFocusScaleListener(this,1);
        holderScaleListener = new VideoListFocusScaleListener(this,2);
        layoutCommonVideoListBinding.videoListComeback.setOnFocusChangeListener(comeBackScaleListener);
        try {
            getData();
        }catch (Exception e){
            Log.e(TAG, "initUI: ", e);
        }


    }

    private void getData()  throws JSONException  {
        JSONObject hotelMessageJson = getHotelMessageFromHttp(serverAddress, tenant);
        if (hotelMessageJson != null){
            String logoUrl = hotelMessageJson.getString("iconUrl");;
            String bgUrl = hotelMessageJson.getString("homepageBackground");
//                初始化背景和logo
            initLogoAndBackGround(VideoListActivity.this,null,logoUrl,layoutCommonVideoListBinding.videoListBackground,bgUrl);
        }

        BackstageHttp.getInstance().getCmsMessage(serverAddress, tenant, id, new BackstageHttp.CmsMessageCallBack() {
            @Override
            public void onCmsMessageResponse(ArrayList<CmsMessageModel> cmsMessageModels) {
                cms.clear();
                cms.addAll(cmsMessageModels);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        设置标题
                        layoutCommonVideoListBinding.title.setText(cms.get(0).getTitle());
                        layoutCommonVideoListBinding.author.setText("导演：" + cms.get(0).getAuthor());
                        layoutCommonVideoListBinding.introduction.setText(cms.get(0).getIntroduction());
                        if (cover == null){
                            cover = "";
                        }
                        Glide.with(VideoListActivity.this)
                                .load(cms.get(0).getPicUrl())
                                .error(R.color.white)
                                .into((layoutCommonVideoListBinding.cover));

                        //                strHtml = cms.get(0).getContent();
                        commonAdapter = new CommonAdapter<CmsMessageModel>(VideoListActivity.this,cms,R.layout.item_video_list) {
                            @Override
                            public void bindData(CommonViewHolder holder, CmsMessageModel data, int position) {
//                        允许焦点
                                holder.getView(R.id.item_all).setFocusable(true);
                                holder.getView(R.id.item_all).setOnFocusChangeListener(holderScaleListener);

                                String item;
                                if (position < 10){
                                    item = "0" + String.valueOf(position + 1);
                                }else {
                                    item = String.valueOf(position + 1);
                                }
                                holder.setText(R.id.item_name,item);

                                holder.setCommonClickListener(new CommonViewHolder.OnCommonItemEventListener() {
                                    @Override
                                    public void onItemClick(View view, int position) {
                                        Intent intent = new Intent(VideoListActivity.this, VideoPlayerActivity.class);
                                        intent.putExtra("url",cms.get(position).getContent());
                                        startActivity(intent);
                                    }

                                    @Override
                                    public void onItemLongClick(int viewId, int position) {

                                    }
                                });

                            }
                        };
                        layoutCommonVideoListBinding.list.setAdapter(commonAdapter);
                        commonAdapter.notifyDataSetChanged();



                    }
                });





            }

            @Override
            public void onCmsMessageFailure(int code, String msg) {

            }
        });

    }



}

