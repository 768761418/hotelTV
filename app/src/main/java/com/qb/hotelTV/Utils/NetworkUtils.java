package com.qb.hotelTV.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;

public class NetworkUtils {

    public static boolean isNetworkAvailable(Context context) {
        // 获取ConnectivityManager实例，用于检查网络连接状态
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // 检查ConnectivityManager是否不为null
        if (connectivityManager != null) {
            // 如果设备运行Android 6.0 (API 23)及以上版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 获取当前网络的NetworkCapabilities对象，表示当前网络的功能和传输类型
                NetworkCapabilities networkCapabilities =
                        connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());

                // 检查NetworkCapabilities是否不为null
                if (networkCapabilities != null) {
                    // 判断当前网络是否通过蜂窝网络、Wi-Fi或以太网连接
                    return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {
                // 如果设备运行Android 6.0以下版本，使用NetworkInfo类检查网络状态
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                // 返回网络是否已连接
                return networkInfo != null && networkInfo.isConnected();
            }
        }
        // 如果没有可用的网络连接，返回false
        return false;
    }

}
