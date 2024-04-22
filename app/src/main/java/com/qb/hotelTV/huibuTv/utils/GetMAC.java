package com.qb.hotelTV.huibuTv.utils;





import com.qb.hotelTV.huibuTv.Channel;
import com.qb.hotelTV.huibuTv.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GetMAC {
    //    获取本机mac地址
    public static String getMacAddress() {
//        判断
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) {
                    continue;
                }
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * eth0 MAC地址获取，适用api9 - api24
     */
    public static String getEth0Mac() {

        String Mac = "";
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("eth0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "Didn\'t get eth0 MAC address";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                    Mac = res1.toString();
                }
                return Mac;
            }
        } catch (Exception ex) {
        }
        return "Didn\'t get eth0 MAC address";
    }

    /**
     * wlan0 MAC地址获取，适用api9 - api24
     */
    public static String getWlan0Mac() {

        String Mac = "";
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "Didn\'t get Wlan0 MAC address";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                    Mac = res1.toString();
                }
                return Mac;
            }
        } catch (Exception ex) {
        }
        return "Didn\'t get Wlan0 address";
    }
    /**
     * 取得 Mac-Address
     * 1. 先取得 eth0 Mac-Address
     * 2. 再取得 wlan0 Mac-Address
     *
     * @return
     */
    public static String getDeviceMacAddress() {
        String mac = !getEth0Mac().equals("Didn\'t get eth0 MAC address") ? getEth0Mac() : getWlan0Mac();
        return mac;
    }

    // 校验mac地址是否得到了授权
    public static String checkMacAddress(String macAddress){
//        在子线程中进行网络请求
        Thread thread= new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String url = "http://"+ MyApplication.getHostIP()+":80/device/manage/check?mac="+macAddress;
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    RequestBody body = RequestBody.create(mediaType, "");
                    Request request = new Request.Builder()
                            .url(url)
                            .method("POST", body)
                            .addHeader("Cookie", "JSESSIONID=d708581a-2cc5-4a48-87bb-3ef93c031c0c")
                            .build();
                    Response response = client.newCall(request).execute();
                    // 解析获取返回的data字段
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String data = jsonObject.getString("data");
                    MyApplication.setAuthorizationStatus(data);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "3";
        }
        return MyApplication.getAuthorizationStatus();
    }
//    获取channel列表
    public static boolean getChannelList(){
        Thread thread= new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String url = "http://"+MyApplication.getHostIP()+":80/channel/manage/listAll";
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    Request request = new Request.Builder()
                            .url(url)
                            .method("GET", null)
                            .addHeader("Cookie", "JSESSIONID=d708581a-2cc5-4a48-87bb-3ef93c031c0c")
                            .build();
                    Response response = client.newCall(request).execute();
                    // 解析获取返回的data字段
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    // 将data字段转换为json数组
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("data"));
                    // 临时的channel
                    ArrayList<Channel> tempChannelList = new ArrayList<>();
                    // 遍历json数组，将每个json对象转换为channel对象，添加到channelList中
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        Channel channel = new Channel(jsonObject1.getString("channelName"),jsonObject1.getString("channelUri"));
                        tempChannelList.add(channel);
                    }
                    //update2 , 解决列表重复的问题，原因是MyApplication没有回收,所以要清除一下列表
                    MyApplication.setChannelsList(tempChannelList);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
