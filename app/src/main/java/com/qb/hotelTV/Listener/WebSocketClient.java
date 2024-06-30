package com.qb.hotelTV.Listener;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketClient extends WebSocketListener {
    private OkHttpClient client;
    private WebSocket webSocket;
    private MessageCallback messageCallback;
    private boolean isConnect = true;

    public WebSocketClient(String url) {
        client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        webSocket = client.newWebSocket(request, this);
        client.dispatcher().executorService().shutdown();
    }

    public interface MessageCallback{
        void onMessageCallback(String data);
    }

    // 新增方法：检查WebSocket连接是否已建立
    public boolean isConnected() {
        return webSocket != null && isConnect;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        System.out.println("WebSocket Opened");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        super.onMessage(webSocket, text);
        System.out.println("Received message: " + text);
        if (text.equals("pong")){
            System.out.println("Received message: " + text);
        }else {
            // 在这里处理接收到的文本消息
            if (messageCallback != null){
                messageCallback.onMessageCallback(text);
            }
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        super.onMessage(webSocket, bytes);
        System.out.println("Received bytes: " + bytes.hex());
        // 在这里处理接收到的字节消息
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        webSocket.close(1000, null);
        System.out.println("WebSocket Closing: " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        isConnect = false;
        t.printStackTrace();
        System.out.println("WebSocket Failure: " + t.getMessage());
    }

    public void sendMessage(String message) {
        webSocket.send(message);
    }

    public void close() {
        webSocket.close(1000, "Goodbye!");
    }

    public void setMessageCallback(MessageCallback messageCallback){
        this.messageCallback = messageCallback;
    }

}
