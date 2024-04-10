package com.qb.hotelTV.Http;

import androidx.annotation.NonNull;

import com.google.gson.JsonArray;
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

public class LocationHttp {
    private static String TAG = LocationHttp.class.getSimpleName();
    //    设置单例
    private LocationHttp() {}
    private static LocationHttp instance = new LocationHttp();
    public static LocationHttp getInstance() {
        return instance;
    }
    static OkHttpClient client = new OkHttpClient();
   static String key = ApiSetting.KEY;

   public interface LocationHttpCallback{
       void onResponse(String responseData);
       void onFailure(String failName);
   }


    public  void getWeather(String location,LocationHttpCallback callback){
//       设置路径
        String url = ApiSetting.URL_WEATHER;
//        添加参数
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder.addQueryParameter("key", key);
        queryUrlBuilder.addQueryParameter("location",location);
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


    public void getGeo(String location,LocationHttpCallback callback){
//       设置路径
       String url = ApiSetting.URL_GEO;
//        添加参数
        HttpUrl.Builder queryUrlBuilder = HttpUrl.get(url).newBuilder();
        queryUrlBuilder.addQueryParameter("key", key);
        queryUrlBuilder.addQueryParameter("location",location);
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
                    String locationName = "";
                    // 使用 Gson 解析 JSON 字符串
                    JsonParser parser = new JsonParser();
                    JsonObject json = parser.parse(responseData).getAsJsonObject();
                    // 获取 code 参数
                    String code = json.get("code").getAsString();
                    if (!code.equals("200")) {
                        callback.onResponse(locationName);
                        return;
                    }
                    // 获取 location 数组
                    JsonArray locationArray = json.getAsJsonArray("location");
                    if (locationArray.size() == 0) {
                        System.out.println("未找到位置信息");
                        return;
                    }
                    // 获取第一个位置对象
                    JsonObject locationObject = locationArray.get(0).getAsJsonObject();
                    // 获取位置名称
                    locationName = locationObject.get("name").getAsString();
                    callback.onResponse(locationName);

                }
            }
        });

    }


//    private static



}
