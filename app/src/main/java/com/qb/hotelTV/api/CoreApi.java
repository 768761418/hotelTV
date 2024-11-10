package com.qb.hotelTV.api;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


/**
 * 登录模块的api
 *
 */
public interface CoreApi {


    @FormUrlEncoded
    @POST("/v2/merchant_api/merchant_scan/addinfo")
    Observable<ResponseBody> addinfo(@Field("username") String username);


    //获取用户权限
    @FormUrlEncoded
    @POST("/interface/wynnUsers/login")
    Observable<ResponseBody> getlist(@Field("username") String user, @Field("password")String password,@Field("device_location")String location,@Field("device_id")String device_id,@Field("write_to_log")boolean write_to_log);



    @GET("admin-api/system/notice/page")
    Observable<ResponseBody> getNoticeList(@Query("pageNo")Integer pageNo,@Query("pageSize")Integer pageSize,@Query("status")Integer status,@Query("type")Integer type);


}
