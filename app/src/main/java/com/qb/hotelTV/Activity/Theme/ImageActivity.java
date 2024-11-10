package com.qb.hotelTV.Activity.Theme;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.dreamgyf.android.ui.widget.textview.marquee.MarqueeTextView;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Model.NoticeResponse;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Utils.GsonUtils;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.api.CoreDataLoader;
import com.qb.hotelTV.api.OnResponseIntercepter;
import com.qb.hotelTV.databinding.ImageLayoutBinding;
import com.qb.hotelTV.huibuTv.MyApplication;

import java.text.ParseException;
import java.util.List;

public class ImageActivity extends ThemeActivity {
    ImageLayoutBinding imageLayoutBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageLayoutBinding = DataBindingUtil.setContentView(this, R.layout.image_layout);
        CoreDataLoader.getInstance().getNoticeList(1,100,0,4).subscribeWith(new OnResponseIntercepter(new OnResponseIntercepter.ResponsePretreatListener() {
            @Override
            public void onfailed(String result, int code) {

            }

            @Override
            public void onSuccess(String result) throws ParseException {
                Log.d(TAG, "onSuccess: "+result);
                NoticeResponse noticeResponse = GsonUtils.jsonToObject(result, NoticeResponse.class);
                List<NoticeResponse.DataDTO.ListDTO> noticeList = noticeResponse.getData().getList();
                if(noticeList!=null&&noticeList.size()>0){
                    for(NoticeResponse.DataDTO.ListDTO temp: noticeList){
                        Log.d(TAG, "onSuccess: "+temp.getUserIds());
                        Log.d(TAG, "onSuccess: "+SharedPreferencesUtils.getInstance(MyApplication.getContext()).loadUserId());
                        if(temp.getUserIds().contains(Integer.parseInt(SharedPreferencesUtils.getInstance(MyApplication.getContext()).loadUserId()))){
                            initUi(temp.getContent());
                            break;
                        }
                    }
                }
            }
        },null));
//        initUi();
    }
    private String TAG = ImageActivity.class.getSimpleName();
    public void getAnnouncements(Context context, String serverAddress, String tenant, MarqueeTextView view){
        BackstageHttp.getInstance().getTvText(serverAddress, tenant, new BackstageHttp.TvTextCallback() {
            @Override
            public void onTvTextResponse(String tvText, String tvTextColor,int code) {
                Log.d(TAG, "onTvTextResponse: " + tvText );
                Log.d(TAG, "onTvTextResponse: " +tvTextColor );
            }

            @Override
            public void onTvTextFailure(int code, String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                    }
                });

            }
        });
    }

    private void initUi(String imageUrl){
        Log.d(TAG, "initUi: "+imageUrl);
        if(imageUrl==null||imageUrl.equals("")){
            Toast.makeText(this,"图片加载失败，请联系管理员！",Toast.LENGTH_LONG).show();
        }
        //加载在线图片呢
        Glide.with(this).load(imageUrl).into(imageLayoutBinding.content);

    }
}
