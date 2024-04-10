package com.qb.hotelTV.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;

//负责请求spv数据
public class WynnDataLoader extends ObjectLoader {
    private LoginApi wynnDataApi;
    private String fl_wynnids = "";
    private String ql_wynnids = "";
    private String noncode_wynnids = "";
    //单例类
    private static WynnDataLoader wynnDataLoader;

    public synchronized static WynnDataLoader getInstance() {
        if (wynnDataLoader == null) wynnDataLoader = new WynnDataLoader();
        return wynnDataLoader;
    }

    public WynnDataLoader() {
        //因为这里qa和prod的路径不一样，所以分成两个api来处理
            this.wynnDataApi = RetrofitClient.getInstance("https://geoapi.qweather.com",LoginApi.class);

    }

    //QL list
    public Observable<ResponseBody> getlist(String location) {
        return observe(wynnDataApi.getlist1("ef1c589fa744404f95c91aa5f87ddc42",location));
    }

}
