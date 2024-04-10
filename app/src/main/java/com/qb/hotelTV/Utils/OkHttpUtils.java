package com.qb.hotelTV.Utils;


import android.util.Log;

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
    public static int HTTP_TIMEOUT = 5;
    //MMA证书密码
//    public static final String CLIENT_WP_SERVER = "Wynn20210511";
    public static final String CLIENT_WP_SERVER = "Cert@20221021"; //old:Wynn20210511
    public static final String CLIENT_WM_SERVER = "P@ssw0rd!!!!";//old:Abcdef123456!
    //GCM证书密码
    public static final String CLIENT_GCM_PASSWORD = "Cert@20221021";//old:Wynn20210511
    //sso服务器证书密码
    public static final String CLIENT_WP_SSO_SERVER_PASSWORD = "Wynn04082020";
    public static final String CLIENT_WM_SSO_SERVER_PASSWORD = "P@ssw0rd20200804";
    //    public static final String CLIENT_INBOX_SERVER_PASSWORD = "Wynn05082020";
    public static final String CLIENT_INBOX_SERVER_PASSWORD = "Wynnwnh20220808!";
    public static final String CLIENT_WP_IDP_SERVER_PASSWORD = "Cert@20230811";
    public static final String CLIENT_WM_IDP_SERVER_PASSWORD = "Gamingbig@wynn!1";
    public static final String CLIENT_WP_WYNNCOIN_SERVER_PASSWORD = "Cert@20221020";
    public static final String CLIENT_MASK_WP_PASSWORD = "Cert@20220825";//old:Cert@20220825
    public static final String CLIENT_MASK_WM_PASSWORD = "Gamingbig@wynn!1";
    public static final String CLIENT_RED_CARD_PASSWORD = "Cert@20221021";
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
                    //对密码进行过滤
                    if (message.contains("password=")) {
                        String[] mes = message.split("&");
                        for (String tmp : mes) {
                            if (tmp.contains("password=")) {
                                message = message.replace(tmp, "password=******");
                            }
                        }
                    }
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
        builder.sslSocketFactory(sslSocketFactory, trustAllManager); //QA测试的时候用这个，全信任模式
        builder.hostnameVerifier((hostname, session) -> true);
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







