package com.qb.hotelTV.huibuTv.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.content.FileProvider;


import com.qb.hotelTV.R;

import java.io.File;

public class InstallUtil {

    private static final String PACKAGE_URL_SCHEME = "package:";
    private static int versionCode;
    private static String versionName;

    /**
     * 是否已安装app
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        try {
            if (TextUtils.isEmpty(packageName))
                return false;
            return context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES) != null;
        } catch (PackageManager.NameNotFoundException localNameNotFoundException) {
            return false;
        }
    }

    /**
     * 打开app
     *
     * @param packageName
     * @param context
     */
    public static void openApp(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent != null)
            context.startActivity(intent);
    }


    /**
     * 获取版本号
     */
    public static final int getVersionCode(Context context) {
        if (versionCode == 0) {
            loadVersionInfo(context);
        }

        return versionCode;
    }

    /**
     * 获取版本名
     */
    public static final String getVersionName(Context context) {
        if (TextUtils.isEmpty(versionName)) {
            loadVersionInfo(context);
        }

        return versionName;
    }

    private static final void loadVersionInfo(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (pi != null) {
                versionCode = pi.versionCode;
                versionName = pi.versionName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 安装apk文件
     */
    public static void installApk(Context context, String filepath) {
        context.startActivity(getInstallApkIntent(filepath));
    }
    public static void installApk(Context context, File file) {
        context.startActivity(getInstallApkIntent(file));
    }

    public static Intent getInstallApkIntent(String filepath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File file = new File(filepath);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        return intent;
    }

    public static Intent getInstallApkIntent(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        return intent;
    }

    public static void install(Context context) {
        String path = Environment.getExternalStorageDirectory() + File.separator +
                context.getPackageName() + File.separator + "apk" + File.separator;
        File file = new File(path, context.getResources().getString(R.string.app_name) + ".apk");
        Intent intent = new Intent(Intent.ACTION_VIEW);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        context.startActivity(intent);
    }

    /**
     * 启动应用的设置
     */
    public static void startAppSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + context.getPackageName()));
        context.startActivity(intent);
    }
}

