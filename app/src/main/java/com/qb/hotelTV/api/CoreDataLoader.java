package com.qb.hotelTV.api;


import com.qb.hotelTV.Activity.CommonActivity.StartupActivity;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.huibuTv.MyApplication;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

//负责请求spv数据
public class CoreDataLoader extends ObjectLoader {
    private CoreApi coreApi;

    //单例类
    private static CoreDataLoader wynnDataLoader;

    public synchronized static CoreDataLoader getInstance() {
        if (wynnDataLoader == null) wynnDataLoader = new CoreDataLoader();
        return wynnDataLoader;
    }

    public CoreDataLoader() {
        //因为这里qa和prod的路径不一样，所以分成两个api来处理
        this.coreApi = RetrofitClient.getInstance(SharedPreferencesUtils.getInstance(MyApplication.getContext()).loadServerAddress()+"/", CoreApi.class);
    }

    //QL list
    public Observable<ResponseBody> getNoticeList(Integer pageNo,Integer pageSize,Integer status,Integer type) {
        return observe(coreApi.getNoticeList(pageNo,pageSize,status,type));
    }

}
