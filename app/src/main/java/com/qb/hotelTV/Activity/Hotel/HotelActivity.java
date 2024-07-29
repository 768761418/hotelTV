package com.qb.hotelTV.Activity.Hotel;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.qb.hotelTV.Activity.BaseActivity;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.databinding.LayoutHotelBinding;

import java.util.ArrayList;

public class HotelActivity extends BaseActivity {
    private LayoutHotelBinding layoutHotelBinding;
    private String strHtml,strTitle;
    private String serverAddress,tenant;
    private ArrayList<CmsMessageModel> cms = new ArrayList<>();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish(); // 结束当前活动
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init(){
        layoutHotelBinding = DataBindingUtil.setContentView(HotelActivity.this, R.layout.layout_hotel);
        String bg = getIntent().getStringExtra("bg");
        Long id = getIntent().getLongExtra("id",-1);
        serverAddress = getIntent().getStringExtra("serverAddress");
        tenant = getIntent().getStringExtra("tenant");
        if (bg != null) {
            Glide.with(HotelActivity.this)
                    .load(bg)
                    .error(R.drawable.app_bg)
                    .into(layoutHotelBinding.hotelBackground);
        }
        if (id != -1 ){
            BackstageHttp.getInstance().getCmsMessage(serverAddress, tenant, id, new BackstageHttp.CmsMessageCallBack() {
                @Override
                public void onCmsMessageResponse(ArrayList<CmsMessageModel> cmsMessageModels) {
                    cms.clear();
                    cms.addAll(cmsMessageModels);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            strHtml = cms.get(0).getContent();
//                            strHtml = "";
                            strTitle = cms.get(0).getTitle();
                            if (strHtml != null && !strHtml.equals("")){
                                layoutHotelBinding.hotelWeb.loadDataWithBaseURL(null,strHtml,"text/html", "UTF-8", null);
                                layoutHotelBinding.hotelWeb.requestFocus();
                                layoutHotelBinding.hotelWeb.setOnFocusChangeListener(focusScaleListener);
                                layoutHotelBinding.hotelTitle.setText(strTitle);
                            }else {
                                layoutHotelBinding.hotelWeb.setVisibility(View.GONE);
                            }
                        }
                    });
                }

                @Override
                public void onCmsMessageFailure(int code, String msg) {

                }
            });
        }

    }
}
