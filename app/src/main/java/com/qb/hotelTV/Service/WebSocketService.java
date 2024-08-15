package com.qb.hotelTV.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.qb.hotelTV.Activity.CommonActivity.SocketNoticeActivity;
import com.qb.hotelTV.Data.CommonData;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Listener.WebSocketClient;

import org.json.JSONException;
import org.json.JSONObject;


public class WebSocketService extends Service {

    private String serverAddress,token;
    private WebSocketClient webSocketClient;
    private Handler handler = new Handler();
    private String TAG = "WebSocketService";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        拿数据用来用
        serverAddress = CommonData.getInstance().getServerAddress();
        token = BackstageHttp.getInstance().getToken();
//        初始化websocket链接
        String webSocketUrl = getWebSocketUrl(serverAddress);
//        初始化websocket
        if (webSocketUrl != null){
            webSocketClient = initWebSocket(this,webSocketUrl);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        关闭的时候销毁websocket的连接
        if (webSocketClient != null){
            Log.d(TAG, "onDestroy: 234" );
            webSocketClient.close();
            webSocketClient = null;
        }
    }



    //    拼接websocket路径
    public String getWebSocketUrl(String serverAddress){
        String token = BackstageHttp.getInstance().getToken();
        if (serverAddress.startsWith("http://")) {
            return serverAddress.replace("http://", "ws://") + "/infra/ws?token=" + token;
        } else if (serverAddress.startsWith("https://")) {
            return serverAddress
                    .replace("https://", "wss://")
                    .replace("/prod-api","") + "/infra/ws?token=" + token;
        }

        return null;
    }

    //    初始化websocket
    public WebSocketClient initWebSocket(Context context, String url){
        WebSocketClient webSocketClient = new WebSocketClient(url);
        Log.d(TAG, "initWebSocket: " + url);
        webSocketClient.setMessageCallback(new WebSocketClient.MessageCallback() {
            @Override
            public void onMessageCallback(String data) {
                try{
                    Log.d(TAG, "initWebSocket: " + data);
                    JSONObject jsonObject = new JSONObject(data);
                    String type = jsonObject.getString("type");
                    if (type.equals("insert-notice")){
//                        由于websocket二级是字符串所以先拿字符串然后转成json
                        String strContent = jsonObject.getString("content");
                        JSONObject contentObject = new JSONObject(strContent);
//                        获取播放信息
                        String content= contentObject.getString("content");
//                        配置信息
                        JSONObject configData = contentObject.getJSONObject("configData");
                        int webType = configData.getInt("type");
                        long webSecond = configData.getLong("second");
                        Log.d(TAG, "onMessageCallback: " + webSecond + "??" + type);
                        Intent intent = new Intent(context, SocketNoticeActivity.class);
                        intent.putExtra("url",content);
                        intent.putExtra("type",webType);
                        intent.putExtra("second",webSecond);
                        // 设置 FLAG_ACTIVITY_NEW_TASK 标志
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }else if (type.equals("insert-notice-delete")) {
                        Intent intent = new Intent("com.qb.hotel.ACTION_START_FINISH_ACTIVITY");
                        Log.d(TAG, "initWebSocket:发送广播 ");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                }catch (JSONException e){
                    Log.e(TAG, "onMessageCallback: " + data, e );
                }
            }
        });
//        长连接
        sendPingToServer(webSocketClient);
        return webSocketClient;
    }

    //    持续发ping给服务器实现长连接
    private void sendPingToServer(WebSocketClient webSocketClient){
        Runnable sendMessageToServer = new Runnable() {
            @Override
            public void run() {
                webSocketClient.sendMessage("ping");
                handler.postDelayed(this, 60*1000);
            }
        };
        handler.post(sendMessageToServer);
    }



}
