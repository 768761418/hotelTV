package com.qb.hotelTV.Http;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.Model.BaseListModel;
import com.qb.hotelTV.Model.BaseResponseModel;
import com.qb.hotelTV.Model.RoomMessageModel;
import com.qb.hotelTV.Setting.ApiSetting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BackstageHttp {
    private static String TAG = LocationHttp.class.getSimpleName();
    //    设置单例
    private BackstageHttp() {}
    private static BackstageHttp instance = new BackstageHttp();
    public static BackstageHttp getInstance() {
        return instance;
    }
    static OkHttpClient client = new OkHttpClient();
    Gson gson = new Gson();


    public interface RoomMessageCallback{
        void onRoomMessageResponse(int id,String roomName,String wifiPassword,String frontDeskPhone);
        void onRoomMessageFailure(int code,String msg);
    }

    public interface ApkCallback{
        void onApkResponse(ArrayList<ApkModel> apkModelArrayList);
        void onApkFailure(int code,String msg);
    }

    public void getRoomMessage(String serverAddress,String roomNumber,RoomMessageCallback callback){
//       设置路径
            String url = "http://"+ serverAddress + ApiSetting.URL_GET_ROOM_MESSAGE;
//        添加参数
            HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
            queryUrlBuilder.addQueryParameter("number", roomNumber);
//        构建请求体
            Request request = new Request.Builder()
                    .url(queryUrlBuilder.build())
                    .addHeader("tenant-id", "1") // 添加请求头
                    .addHeader("Authorization", ApiSetting.AUTHORIZATION) // 添加请求头
                    .build();
//        接受回调
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Log.e(TAG, "onRoomMessageFailure" + url,e);
                    String msg = "请输入正确的服务器";
                    int code = -1;
                    callback.onRoomMessageFailure(code,msg);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        String responseData = response.body().string();
                        BaseResponseModel<RoomMessageModel> roomMessage = gson.fromJson(responseData,new TypeToken<BaseResponseModel<RoomMessageModel>>(){}.getType());
                        Integer code = roomMessage.getCode();

                        if (code != 0) {
                            callback.onRoomMessageResponse(0,"","","");
                            return;
                        }

                        // 从 JsonObject 中提取所需的字段
                        Integer id = roomMessage.getData().getId();
                        String roomName = roomMessage.getData().getRoomName();
                        String wifiPassword = roomMessage.getData().getWifiPassword();
                        String frontDeskPhone = roomMessage.getData().getFrontDeskPhone();
                        callback.onRoomMessageResponse(id,roomName,wifiPassword,frontDeskPhone);

                    }
                }
            });
    }


    public void getApk(String serverAddress,ApkCallback callback){
//       设置路径
        String url = "http://"+ serverAddress + ApiSetting.URL_GET_APK;
//        添加参数
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder.addQueryParameter("pageNo", ApiSetting.PAGE_NO);
        queryUrlBuilder.addQueryParameter("pageSize", ApiSetting.PAGE_SIZE);

//        构建请求体
        Request request = new Request.Builder()
                .url(queryUrlBuilder.build())
                .addHeader("tenant-id", "1") // 添加请求头
                .addHeader("Authorization", ApiSetting.AUTHORIZATION) // 添加请求头
                .build();
//        接受回调
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "onRoomMessageFailure" + url,e);
                String msg = "请输入正确的服务器";
                int code = -1;
                callback.onApkFailure(code,msg);
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    String responseData = response.body().string();
                    BaseResponseModel<BaseListModel<ApkModel>> apk = gson.fromJson(responseData,new TypeToken<BaseResponseModel<BaseListModel<ApkModel>>>(){}.getType());
                    Integer code = apk.getCode();

                    if (code != 0) {
                        callback.onApkResponse(null);
                        return;
                    }
                    ArrayList<ApkModel> apkModelArrayList = apk.getData().getList();
                    callback.onApkResponse(apkModelArrayList);
                }
            }
        });
    }




}
