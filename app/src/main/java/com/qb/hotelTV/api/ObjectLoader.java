package com.qb.hotelTV.api;


import io.reactivex.Observable;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

//通用获取observable 并处理业务
public class ObjectLoader {

    //将重复的操作提取出来
    protected <T> Observable<T> observe(Observable<T> observable){
        return observable
                .subscribeOn(Schedulers.io())//切换线程去进行网络请求
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());//请求到的数据切换到主线程
    }

}
