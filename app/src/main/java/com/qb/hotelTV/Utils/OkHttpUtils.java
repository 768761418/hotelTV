package com.qb.hotelTV.Utils;


import android.util.Log;

import com.qb.hotelTV.api.CommonRequestInterceptor;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * okHttpclient 模式的单例类
 * Created by cmt on 2017/9/28.
 */

public class OkHttpUtils {
    public final static int CONNECT_TIMEOUT = 300;
    public final static int READ_TIMEOUT = 300;
    public final static int WRITE_TIMEOUT = 300;

    private static OkHttpClient client = null;
    private static HttpLoggingInterceptor httpLoggingInterceptor;

    private OkHttpUtils() {

    }

    public static OkHttpClient getInstance() {
        if (client == null) {
            synchronized (OkHttpUtils.class) {
                if (client == null)
                    client = new OkHttpClient().newBuilder().build();
            }
        }
        return client;
    }


    public static HttpLoggingInterceptor getHttpLoggingInterceptor() {
        if (httpLoggingInterceptor == null) {
            httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                @Override
                public void log(String message) {
                    Log.d("OkHttp3", "url = " + message);
                }
            });
        }
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    /**
     * 多baseurl,多https证书
     *
     * @return
     */

    public static OkHttpClient.Builder getBuilder() {
        //全信任证书模式，可能无法通过双向的证书验证
        TrustAllManager trustAllManager = new TrustAllManager();
        SSLSocketFactory sslSocketFactory = OkHttpUtils.createTrustAllSSLFactory(trustAllManager);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);//设置连接超时间
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS); //写操作超时时间
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS); //读操作超时时间
        builder.retryOnConnectionFailure(true); //错误重连
        builder.hostnameVerifier((hostname, session) -> true);
        builder.addInterceptor(new CommonRequestInterceptor());
        builder.addInterceptor(getHttpLoggingInterceptor()); //请求日志拦截
        builder.protocols(Collections.singletonList(Protocol.HTTP_1_1));
        return builder;
    }

    //配置连接超时时间，缓存，拦截器
    public static SSLSocketFactory createTrustAllSSLFactory(TrustAllManager trustAllManager) {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{trustAllManager}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return ssfFactory;
    }

    static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[0];
        }
    }
}

    //设置api 的请求hostr=gcm_7997







