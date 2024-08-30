package com.qb.hotelTV.Http;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.Model.BaseListModel;
import com.qb.hotelTV.Model.BaseResponseModel;
import com.qb.hotelTV.Model.CmsMessageModel;
import com.qb.hotelTV.Model.HotelListModel;
import com.qb.hotelTV.Model.HotelMessageModel;
import com.qb.hotelTV.Model.RoomMessageModel;
import com.qb.hotelTV.Model.StartData;
import com.qb.hotelTV.Model.TvTextModel;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.Setting.ApiSetting;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BackstageHttp {
    private static  final  String TAG = LocationHttp.class.getSimpleName();
    //    设置单例
    private BackstageHttp() {}
    private static BackstageHttp instance = new BackstageHttp();
    public static BackstageHttp getInstance() {
        return instance;
    }
    // 设置5秒超时
    static OkHttpClient client = new OkHttpClient.Builder()
//            连接超时时间为5 秒钟
            .connectTimeout(5, TimeUnit.SECONDS)
//            设置了读取超时时间
            .readTimeout(5, TimeUnit.SECONDS)
//            设置了写入超时时间
            .writeTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(new RetryInterceptor(3))
            .build();

    Gson gson = new Gson();
    private String Authorization;
    private String token = null;


    public interface TvTextCallback{
        void onTvTextResponse(String tvText,String tvTextColor,int code);
        void onTvTextFailure(int code,String msg);
    }


    public interface CmsMessageCallBack{
        void onCmsMessageResponse(ArrayList<CmsMessageModel> cmsMessageModels);
        void onCmsMessageFailure(int code,String msg);
    }

    public String getToken(){
        return token;
    }
    public void setToken(String token){
        this.token = token;
    }

    public String getAuthorization(){return Authorization;}
    public void setAuthorization(String authorization){
        this.Authorization = authorization;
    }

//  同步  登录函数
    public String[] loginSystem(String serverAddress,String roomNumber,String tenant) {
//       设置路径
        String url = serverAddress + ApiSetting.LOGIN_API;
        Log.d(TAG, "loginSystem: " + serverAddress);
        Log.d(TAG, "loginSystem: " + roomNumber);
        Log.d(TAG, "loginSystem: " + tenant);
        Log.d(TAG, "loginSystem: " + url);

        JSONObject json;
        String[] result = new String[2];
        try{
            json = new JSONObject();
            json.put("username",roomNumber);
            json.put("password",ApiSetting.PASSWORD);
            json.put("isCreate",1);
        }catch (JSONException e){
            json = null;
            Log.e(TAG, "loginSystem: ", e);
        }

        if (json != null){
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json.toString());
            Request request = new  Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .addHeader("tenant-id", tenant)
                    .build();

            Call call = client.newCall(request);

            try {
                Response response = call.execute();
                String responseData = response.body().string();
                Log.d(TAG, "loginSystem: " +responseData);
                if (response.isSuccessful()){

                    JSONObject jsonObject = new JSONObject(responseData);
                    long code = jsonObject.getLong("code");
                    Log.d(TAG, "loginSystem: " + responseData);
                    if (code == 0L){
                        result[0] = String.valueOf(code);
                        result[1] = jsonObject.getJSONObject("data").getString("accessToken");
                        token = result[1];
                    }else {
                        result[0] = String.valueOf(code);
                        result[1] = jsonObject.getString("msg");
                    }

                }
            }catch (Exception e){
                Log.e(TAG, "loginSystem: ", e);
            }
            Authorization="Bearer " + token;
            Log.d(TAG, "loginSystem: " + Authorization);
        }
        return  result;
    }



//   同步 获取房间信息
    public JSONObject getRoomMessage(String serverAddress,String roomNumber,String tenant){
//       设置路径
        String url = serverAddress + ApiSetting.URL_GET_ROOM_MESSAGE;
        Log.d(TAG, "请求路径: " + url);
//        添加参数
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder.addQueryParameter("number", roomNumber);
        Log.d(TAG, "获取房间信息请求参数: " + tenant);

//        构建请求体
            Request request = new Request.Builder()
                    .url(queryUrlBuilder.build())
                    .addHeader("tenant-id", tenant) // 添加请求头
                    .addHeader("Authorization", Authorization) // 添加请求头
                    .build();
//        接受回调
            Call call = client.newCall(request);
            JSONObject data = null;
            try {
                Response response = call.execute();
                String responseData = response.body().string();
                Log.d(TAG, "获取房间信息: " + responseData);
                if (response.isSuccessful()){
                    JSONObject jsonObject = new JSONObject(responseData);
                    int code = jsonObject.getInt("code");
                    if (code == 0){
                        data = jsonObject.getJSONObject("data");
                    }
                }
            } catch (Exception e){
                Log.e(TAG, "请求房间信息错误: ", e);
            }
            return  data;
    }


//    同步 获取app列表
    public ArrayList<ApkModel> getApk(String serverAddress,String tenant){
//       设置路径
        String url = serverAddress + ApiSetting.URL_GET_APK;
        ArrayList<ApkModel> apkModelArrayList = new ArrayList<>();
        Log.d(TAG, "请求路径: " + url);
//        添加参数
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder.addQueryParameter("pageNo", ApiSetting.PAGE_NO);
        queryUrlBuilder.addQueryParameter("pageSize", ApiSetting.PAGE_SIZE);

//        构建请求体
        Request request = new Request.Builder()
                .url(queryUrlBuilder.build())
                .addHeader("tenant-id", tenant) // 添加请求头
                .addHeader("Authorization", Authorization) // 添加请求头
                .build();
//        接受回调
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            if(response.isSuccessful()){
                String responseData = response.body().string();
                Log.d(TAG, "apk请求结果：" + responseData);
                BaseResponseModel<BaseListModel<ApkModel>> apk = gson.fromJson(responseData,new TypeToken<BaseResponseModel<BaseListModel<ApkModel>>>(){}.getType());
                Integer code = apk.getCode();
                if (code == 0) {
                    apkModelArrayList  = apk.getData().getList();
                }
            }
            }catch (Exception e){
            Log.e(TAG, "获取apk列表报错：", e);
        }
        return apkModelArrayList;
    }


//  同步  获取配置,包括背景，logo等,返回一个json
    public JSONObject getHotelMessage(String serverAddress,String tenant){
//       设置路径
        String url =serverAddress + ApiSetting.URL_GET_HOTEL_MESSAGE;
        Log.d(TAG, "请求路径: " + url);
//        添加参数
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();

//        构建请求体
        Request request = new Request.Builder()
                .url(queryUrlBuilder.build())
                .addHeader("tenant-id", tenant) // 添加请求头
                .addHeader("Authorization", Authorization) // 添加请求头
                .build();
//        接受回调
        Call call = client.newCall(request);
        JSONObject hotelMessageJson = null;
        try{
            Response response = call.execute();
            if (response.isSuccessful()){
                String responseData = response.body().string();
                Log.d(TAG, "配置请求结果：" + responseData);
                JSONObject jsonObject = new JSONObject(responseData);
                int code = jsonObject.getInt("code");
//                如果不为0则请求失败不做处理
                if (code == 0){
                    hotelMessageJson = jsonObject.getJSONObject("data");
                }

            }

        }catch (IOException e){
            Log.e(TAG, "getHotelMessage: ",e );
        } catch (JSONException e) {
            Log.e(TAG, "getHotelMessage: ",e );
        }
        return hotelMessageJson;
    }


//  异步 获取公告
    public void getTvText(String serverAddress,String tenant,TvTextCallback callback){
//       设置路径
        String url =serverAddress + ApiSetting.URL_GET_TV_TEXT;
        Log.d(TAG, "请求路径: " + url);
//        添加参数
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder.addQueryParameter("pageNo", ApiSetting.PAGE_NO);
        queryUrlBuilder.addQueryParameter("pageSize", ApiSetting.PAGE_SIZE);

//        构建请求体
        Request request = new Request.Builder()
                .url(queryUrlBuilder.build())
                .addHeader("tenant-id", tenant) // 添加请求头
                .addHeader("Authorization", Authorization) // 添加请求头
                .build();
//        接受回调
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onRoomMessageFailure" + url,e);
                String msg = "请输入正确的服务器";
                int code = -1;
                callback.onTvTextFailure(code,msg);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    Log.d(TAG, "请求结果4" + responseData);
                    BaseResponseModel<BaseListModel<TvTextModel>> tvText = gson.fromJson(responseData,new TypeToken<BaseResponseModel<BaseListModel<TvTextModel>>>(){}.getType());
                    Integer code = tvText.getCode();

                    if (code != 0) {
                        callback.onTvTextResponse("","#000000",code);
                        return;
                    }
                    ArrayList<TvTextModel> tvTextModelsArrayList = tvText.getData().getList();
                    StringBuilder stringBuilder = new StringBuilder();
                    String firstTextColor = ""; // 默认值
                    boolean isTop = true;

                    for (int i = 0; i < tvTextModelsArrayList.size(); i++) {
                        TvTextModel model = tvTextModelsArrayList.get(i);
                        int type = model.getType();
                        if (type != 3){

                            String title = model.getTitle();
                            String content = model.getContent();
                            int status = model.getStatus();
                            if (isTop) {
                                firstTextColor = model.getTextColor(); // 获取第一个数据的textColor
                                isTop = false;
                            }
                            if (status == 0){
                                stringBuilder.append(title).append(": ").append(content);
                            }
                        }
                    }
                    String tvTextStr = stringBuilder.toString();
                    Log.d(TAG, "tvTextStr: " +tvTextStr);
                    callback.onTvTextResponse(tvTextStr,firstTextColor,code);
                }
            }
        });
    }


//  同步 获取电视频道
    public ArrayList<VideoModel> getTvChannel(String serverAddress,String tenant){
//       设置路径
        String url =serverAddress + ApiSetting.URL_GET_TV_CHANNEL;
        Log.d(TAG, "获取电视界面请求路径: " + url);
//        添加参数
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder.addQueryParameter("pageNo", ApiSetting.PAGE_NO);
        queryUrlBuilder.addQueryParameter("pageSize", ApiSetting.PAGE_SIZE);

//        构建请求体
        Request request = new Request.Builder()
                .url(queryUrlBuilder.build())
                .addHeader("tenant-id", tenant) // 添加请求头
                .addHeader("Authorization", Authorization) // 添加请求头
                .build();
//        接受回调
        Call call = client.newCall(request);
        ArrayList<VideoModel> videoModels = new ArrayList<>();
        try{
            Response response = call.execute();
            String responseData = response.body().string();
            Log.d(TAG, "获取电视界面：" + responseData);
            if (response.isSuccessful()){
                BaseResponseModel<BaseListModel<VideoModel>> tvChannel = gson.fromJson(responseData,new TypeToken<BaseResponseModel<BaseListModel<VideoModel>>>(){}.getType());
                int code = tvChannel.getCode();
                if (code != 0) {
                    Log.d(TAG, "请求失败" + url + "//" + responseData);
                    return videoModels;
                }
                videoModels = tvChannel.getData().getList();
            }
        }catch (Exception e){
                Log.e(TAG, "errorHttp",e );
        }
        return  videoModels;
    }

//    同步 获取主界面模块
    public ArrayList<HotelListModel> getHotelList(String serverAddress,String tenant,int listNumber){
//       设置路径
        String url =serverAddress + ApiSetting.URL_GET_HOTEL_LIST;
        Log.d(TAG, "请求路径: " + url);
//        添加参数
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder.addQueryParameter("pageNo", ApiSetting.PAGE_NO);
        queryUrlBuilder.addQueryParameter("pageSize", ApiSetting.PAGE_SIZE);

//        构建请求体
        Request request = new Request.Builder()
                .url(queryUrlBuilder.build())
                .addHeader("tenant-id", tenant) // 添加请求头
                .addHeader("Authorization", Authorization) // 添加请求头
                .build();
//        接受回调
        Call call = client.newCall(request);
        ArrayList<HotelListModel> hotelListModels = new ArrayList<>();
        try {
            Response response = call.execute();
            String responseData = response.body().string();
                Log.d(TAG, "请求结果6" + responseData);
                if(response.isSuccessful()){
                    BaseResponseModel<BaseListModel<HotelListModel>> hotelList = gson.fromJson(responseData,new TypeToken<BaseResponseModel<BaseListModel<HotelListModel>>>(){}.getType());
                    int code = hotelList.getCode();
                    if (code != 0 ){
                        return hotelListModels;
                    }
                    hotelListModels = hotelList.getData().getList();
                        // 获取前四个元素的子列表
                    List<HotelListModel> subList = hotelListModels.subList(0, Math.min(hotelListModels.size(), listNumber));
                        // 创建一个新的 ArrayList 以包含子列表中的元素
                    ArrayList<HotelListModel> firstFourElements = new ArrayList<>(subList);
                        // 如果需要将原来的列表替换为前四个元素
                    hotelListModels.clear();
                    hotelListModels.addAll(firstFourElements);
                    Log.d(TAG, "getHotelList: " + gson.toJson(hotelListModels));
                }

        }catch (Exception e){
            Log.e(TAG, "请求酒店列表失败： ", e);
        }
        return hotelListModels;
    }



//    获取文章管理
    public void getCmsMessage(String serverAddress,String tenant,Long id,CmsMessageCallBack callBack) {
//       设置路径
        String url = serverAddress + ApiSetting.URL_GET_CMS_MESSAGE;
        Log.d(TAG, "请求路径: " + url);
//        添加参数
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder.addQueryParameter("pageNo", ApiSetting.PAGE_NO);
        queryUrlBuilder.addQueryParameter("pageSize", ApiSetting.PAGE_SIZE);
        queryUrlBuilder.addQueryParameter("categoryId", String.valueOf(id));
        queryUrlBuilder.addQueryParameter("status","0");

//        构建请求体
        Request request = new Request.Builder()
                .url(queryUrlBuilder.build())
                .addHeader("tenant-id", tenant) // 添加请求头
                .addHeader("Authorization", Authorization) // 添加请求头
                .build();
//        接受回调
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onFailure: ", e);
                String msg = "请输入正确的服务器";
                int code = -1;
                callBack.onCmsMessageFailure(code, msg);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG, "请求结果7" + responseData);
                if (response.isSuccessful()) {
                    try {
                        BaseResponseModel<BaseListModel<CmsMessageModel>> cmsMessage = gson.fromJson(responseData, new TypeToken<BaseResponseModel<BaseListModel<CmsMessageModel>>>() {
                        }.getType());
                        int code = cmsMessage.getCode();
                        if (code != 0) {
                            String msg = "请输入正确的服务器";
                            callBack.onCmsMessageFailure(code, msg);
                        }
                        ArrayList<CmsMessageModel> cmsMessageModels = cmsMessage.getData().getList();
                        Log.d(TAG, "getHotelList1112: " + gson.toJson(cmsMessageModels));
                        // 获取1个元素的子列表
                        List<CmsMessageModel> subList = cmsMessageModels.subList(0, Math.min(cmsMessageModels.size(), 2));

                        // 创建一个新的 ArrayList 以包含子列表中的元素
                        ArrayList<CmsMessageModel> firstFourElements = new ArrayList<>(subList);
                        cmsMessageModels.clear();
                        cmsMessageModels.addAll(firstFourElements);
                        Log.d(TAG, "getHotelList1113: " + gson.toJson(cmsMessageModels));
                        callBack.onCmsMessageResponse(cmsMessageModels);

                    } catch (Exception e) {
                        Log.e(TAG, "errorHttp", e);
                        String msg = "请输入正确的服务器";
                        int code = -1;
                        callBack.onCmsMessageFailure(code, msg);
                    }

                }
            }
        });
    }

    //获取文章管理分页
    public void getCmsMessage(String serverAddress,String tenant,Long id,int page_no,CmsMessageCallBack callBack) {
        String page_size = "6";
        String page_number = String.valueOf(page_no);
//       设置路径
        String url = serverAddress + ApiSetting.URL_GET_CMS_MESSAGE;
        Log.d(TAG, "请求路径: " + url);
//        添加参数
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder.addQueryParameter("pageNo", page_number);
        queryUrlBuilder.addQueryParameter("pageSize",page_size);
        queryUrlBuilder.addQueryParameter("categoryId", String.valueOf(id));
        queryUrlBuilder.addQueryParameter("status","0");


//        构建请求体
        Request request = new Request.Builder()
                .url(queryUrlBuilder.build())
                .addHeader("tenant-id", tenant) // 添加请求头
                .addHeader("Authorization", Authorization) // 添加请求头
                .build();
//        接受回调
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onFailure: ", e);
                String msg = "请输入正确的服务器";
                int code = -1;
                callBack.onCmsMessageFailure(code, msg);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                Log.d(TAG, "请求结果7" + responseData);
                if (response.isSuccessful()) {
                    try {
                        BaseResponseModel<BaseListModel<CmsMessageModel>> cmsMessage = gson.fromJson(responseData, new TypeToken<BaseResponseModel<BaseListModel<CmsMessageModel>>>() {
                        }.getType());
                        int code = cmsMessage.getCode();
                        if (code != 0) {
                            String msg = "请输入正确的服务器";
                            callBack.onCmsMessageFailure(code, msg);
                        }
                        ArrayList<CmsMessageModel> cmsMessageModels = cmsMessage.getData().getList();
                        callBack.onCmsMessageResponse(cmsMessageModels);

                    } catch (Exception e) {
                        Log.e(TAG, "errorHttp", e);
                        String msg = "请输入正确的服务器";
                        int code = -1;
                        callBack.onCmsMessageFailure(code, msg);
                    }

                }
            }
        });
    }

    public String[] getAppMessage(String serverAddress,String tenant,Long id){
        String[] result = new String[2];
        result[0] = null;
        result[1] = null;

        String url = serverAddress + ApiSetting.URL_GET_CMS_MESSAGE;
        Log.d(TAG, "请求路径: " + url);
//        添加参数
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder.addQueryParameter("pageNo", ApiSetting.PAGE_NO);
        queryUrlBuilder.addQueryParameter("pageSize", ApiSetting.PAGE_SIZE);
        queryUrlBuilder.addQueryParameter("categoryId", String.valueOf(id));
        queryUrlBuilder.addQueryParameter("status","0");

//        构建请求体
        Request request = new Request.Builder()
                .url(queryUrlBuilder.build())
                .addHeader("tenant-id", tenant) // 添加请求头
                .addHeader("Authorization", Authorization) // 添加请求头
                .build();
//        接受回调
        Call call = client.newCall(request);
        try {
            Response response = call.execute();
            String responseData = response.body().string();
            if (response.isSuccessful()){
                BaseResponseModel<BaseListModel<CmsMessageModel>> cmsMessage = gson.fromJson(responseData, new TypeToken<BaseResponseModel<BaseListModel<CmsMessageModel>>>() {
                }.getType());
                ArrayList<CmsMessageModel> cmsMessageModels = cmsMessage.getData().getList();
                result[0] = cmsMessageModels.get(0).getVar1();
                result[1] = cmsMessageModels.get(0).getContent();
            }
        }catch (IOException e){
            Log.e(TAG, "getAppMessage: ", e);
        }
        return result;
    }

    //    检查版本
    public Call getAppVersion(String serverAddress,String tenant){
        String url = serverAddress + ApiSetting.URL_GET_APP_VERSION;

        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder.addQueryParameter("pageNo", ApiSetting.PAGE_NO);
        queryUrlBuilder.addQueryParameter("pageSize", ApiSetting.PAGE_SIZE);

//        构建请求体
        Request request = new Request.Builder()
                .url(queryUrlBuilder.build())
                .addHeader("tenant-id", tenant) // 添加请求头
                .addHeader("Authorization", Authorization) // 添加请求头
                .build();
        Call call = client.newCall(request);
        return call;
    }

}
