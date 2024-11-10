package com.qb.hotelTV.huibuTv;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.cdnbye.core.p2p.P2pConfig;
import com.cdnbye.core.utils.AnnounceLocation;
import com.cdnbye.sdk.P2pEngine;
import com.qb.hotelTV.Data.CommonData;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Model.VideoModel;
import com.qb.hotelTV.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;


public class MyApplication extends Application {
    static Context context;


    private static ArrayList<Channel> channelsList = new ArrayList<Channel>();
    static ArrayList<VideoModel> videoList = new ArrayList<>();
    private static int channelIndex = 0;
    private static String hostIP;
    private static String authorizationStatus;
    private static String updateData;
    private static final String  TAG = "MyApplication";
    private static final String KEY_SERVER_ADDRESS = "server_address";
    private static final String KEY_ROOM_NUMBER = "room_number";
    private static final String KEY_TENANT = "tenant";
    private static final String PREFS_NAME = "Hospital";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate1: ");
//        获取strings.xml中的字符串
        context = getApplicationContext();
        hostIP = getString(R.string.hostAddress);
        P2pConfig config = new P2pConfig.Builder()
                .announceLocation(AnnounceLocation.China)    // Set HongKong or USA if you changed zone
                .build();
        P2pEngine.init(this, getString(R.string.p2pToken), config);
        initChannels();
//        initUpdate();

    }

    public static Context getContext(){
        return context;
    }
    public static void initChannels() {
//        channelsList.add(new Channel("CCTV-1", "http://183.63.15.42:9901/tsfile/live/0001_1.m3u8"));
        channelsList.add(new Channel("南宁公共频道", "http://play-flive.ifeng.com/live/06OLEEWQKN4.m3u8"));
        channelsList.add(new Channel("南宁新闻综合频道", "http://play-flive.ifeng.com/live/06OLEEWQKN4.m3u8"));
        channelsList.add(new Channel("南宁新闻综合频道", "http://play-flive.ifeng.com/live/06OLEEWQKN4.m3u8"));
        channelsList.add(new Channel("南宁都市生活频道", "http://play-flive.ifeng.com/live/06OLEEWQKN4.m3u8"));
        channelsList.add(new Channel("南宁影视娱乐频道", "http://play-flive.ifeng.com/live/06OLEEWQKN4.m3u8"));
    }










    public static void setVideoList(ArrayList<VideoModel> videoList) {
        MyApplication.videoList = videoList;
    }
////    初始化自动更新组件
//    private void initUpdate(){
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.connectTimeout(30_000, TimeUnit.SECONDS)
//                .readTimeout(30_000, TimeUnit.SECONDS)
//                .writeTimeout(30_000, TimeUnit.SECONDS)
//                //如果你需要信任所有的证书，可解决根证书不被信任导致无法下载的问题 start
//                .sslSocketFactory(SSLUtils.createSSLSocketFactory())
//                .hostnameVerifier(new SSLUtils.TrustAllHostnameVerifier())
//                //如果你需要信任所有的证书，可解决根证书不被信任导致无法下载的问题 end
//                .retryOnConnectionFailure(true);
//        //如果你想使用okhttp作为下载的载体，那么你需要自己依赖okhttp，更新库不强制依赖okhttp！可以使用如下代码创建一个OkHttpClient 并在UpdateConfig中配置setCustomDownloadConnectionCreator end
//        //当你希望使用传入model的方式，让插件自己解析并实现更新
//        UpdateConfig updateConfig = new UpdateConfig()
//                .setDebug(true)//是否是Debug模式1
//                .setDataSourceType(TypeConfig.DATA_SOURCE_TYPE_MODEL)//设置获取更新信息的方式
//                .setShowNotification(true)//配置更新的过程中是否在通知栏显示进度
//                .setNotificationIconRes(R.mipmap.ic_launcher)//配置通知栏显示的图标
//                .setUiThemeType(TypeConfig.UI_THEME_CUSTOM)//配置UI的样式，一种有12种样式可供选择
//                .setAutoDownloadBackground(false)//是否需要后台静默下载，如果设置为true，则调用checkUpdate方法之后会直接下载安装，不会弹出更新页面。当你选择UI样式为TypeConfig.UI_THEME_CUSTOM，静默安装失效，您需要在自定义的Activity中自主实现静默下载，使用这种方式的时候建议setShowNotification(false)，这样基本上用户就会对下载无感知了
//                .setCustomActivityClass(MyUpdateActivity.class)//如果你选择的UI样式为TypeConfig.UI_THEME_CUSTOM，那么你需要自定义一个Activity继承自RootActivity，并参照demo实现功能，在此处填写自定义Activity的class
//                .setNeedFileMD5Check(false)//是否需要进行文件的MD5检验，如果开启需要提供文件本身正确的MD5校验码，DEMO中提供了获取文件MD5检验码的工具页面，也提供了加密工具类Md5Utils
//                .setCustomDownloadConnectionCreator(new OkHttp3Connection.Creator(builder));//如果你想使用okhttp作为下载的载体，可以使用如下代码创建一个OkHttpClient，并使用demo中提供的OkHttp3Connection构建一个ConnectionCreator传入，在这里可以配置信任所有的证书，可解决根证书不被信任导致无法下载apk的问题
//        AppUpdateUtils.init(this, updateConfig);
//    }

    public static ArrayList<Channel> getChannelsList() {
        initChannels();
        return channelsList;
    }

    public static void setChannelsList(ArrayList<Channel> channelsList) {
        MyApplication.channelsList = channelsList;
    }

    public static int getChannelIndex() {
        return channelIndex;
    }

    public static void setChannelIndex(int channelIndex) {
        MyApplication.channelIndex = channelIndex;
    }

    public static String getHostIP() {
        return hostIP;
    }

    public void setHostIP(String hostIP) {
        this.hostIP = hostIP;
    }

    public static String getAuthorizationStatus() {
        return authorizationStatus;
    }

    public static void setAuthorizationStatus(String authorizationStatus) {
        MyApplication.authorizationStatus = authorizationStatus;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
    // 获取当前app的版本号
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = pi.versionCode;
            if (versionCode <= 0) {
                return 0;
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionCode;
    }

    public static String getUpdateData() {
        return updateData;
    }

    public static void setUpdateData(String updateData) {
        MyApplication.updateData = updateData;
    }
}
