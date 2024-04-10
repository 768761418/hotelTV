package com.qb.hotelTV.api;


import com.qb.hotelTV.Setting.ApiSetting;
import com.qb.hotelTV.Utils.OkHttpUtils;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

// Retrofit
public class RetrofitClient {


    private static final int DEFAULT_TIME_OUT = 5;
    public static<T> T getInstance(String api_host,Class<T> serveice){
          Retrofit  client = new Retrofit.Builder()
                    .client(OkHttpUtils.getBuilder().build()) //okhttpClickent 参数拦截
                    .baseUrl(api_host)//默认访问地址
                    .addConverterFactory(GsonConverterFactory.create())  //将json 转化成bean
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        return client.create(serveice);
    }

    /**
     * 用户多baseUrl且每个url对应需要https
     * @param type
     * @return
     */



}
