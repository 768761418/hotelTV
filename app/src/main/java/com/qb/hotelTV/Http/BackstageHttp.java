package com.qb.hotelTV.Http;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.qb.hotelTV.Setting.ApiSetting;

import java.io.IOException;

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


    public interface BackstageHttpCallback{
        void onResponse(String responseData);
        void onFailure(String failName);
    }

    public void getRoomMessage(String serverAddress,String roomNumber,BackstageHttpCallback callback){
//       设置路径
            String url = ApiSetting.URL_WEATHER;
//        添加参数
            HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
            queryUrlBuilder.addQueryParameter("number", roomNumber);
//        构建请求体
            Request request = new Request.Builder()
                    .url(queryUrlBuilder.build())
                    .build();
//        接受回调
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    String failName = "";
                    callback.onFailure(failName);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if(response.isSuccessful()){
                        String responseData = response.body().string();
                        String weather = "";
                        // 使用 Gson 解析 JSON 字符串
                        JsonParser parser = new JsonParser();
                        JsonObject json = parser.parse(responseData).getAsJsonObject();
                        String code = json.get("code").getAsString();
                        if (!code.equals("200")) {
                            callback.onResponse(weather);
                            return;
                        }
                        // 从 JsonObject 中提取所需的字段
                        weather = json.getAsJsonObject("now").get("text").getAsString();
                        callback.onResponse(weather);

                    }
                }
            });



    }




}
