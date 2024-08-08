package com.qb.hotelTV.Utils;

import static com.qb.hotelTV.Utils.LoadUtils.dismissProgressDialog;
import static com.qb.hotelTV.Utils.LoadUtils.showProgressDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.maning.updatelibrary.InstallUtils;
import com.qb.hotelTV.Const;
import com.qb.hotelTV.Setting.DownloadSetting;
import com.qb.hotelTV.Setting.ProgressDialogSetting;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownLoadUtil {

    private Context context;
    private OkHttpClient client;
    private String TAG = "DownLoadUtil";

    public DownLoadUtil(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
    }

    // 文件下载完成后的回调接口
    public interface FileDownloadCallback {
        void onFileReady(String filePath);
    }

    // 从URL中提取文件名
    private String getFileNameFromUrl(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

    /**
     * 检查指定路径下是否存在指定名称的文件
     * @param fileName 要检查的文件名
     * @param filePath 要检查的文件路径
     * @return 如果文件存在，则返回true；否则返回false
     */
    public static boolean isFileExists(String fileName, String filePath) {
        File file = new File(filePath, fileName);
        return file.exists();
    }


    /**
     * 检查文件是否存在，如果不存在则下载文件，并在操作完成后回调文件路径
     * @param url 文件的下载地址
     * @param callback 下载完成后回调
     */
    public void inspectOrDownloadFile(String url, FileDownloadCallback callback) {
        // 从URL中提取文件名
        String fileName = getFileNameFromUrl(url);
        // 构建文件保存路径
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);

        // 检查文件是否存在
        if (isFileExists(fileName, context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath())) {
            // 文件已存在，直接回调文件路径
            callback.onFileReady(file.getAbsolutePath());
            Log.d(TAG, "路径拿取: "+file.getAbsolutePath());
        } else {
            // 文件不存在，开始下载
            downloadFile(url, file, callback);
        }
    }



    // 下载文件
    private void downloadFile(String url, File file, FileDownloadCallback callback) {
        // 构建请求
        Request request = new Request.Builder().url(url).build();

        // 发起异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                // 在主线程上显示下载失败的Toast
                new Handler(Looper.getMainLooper()).post(() -> {
//                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 在主线程上显示下载失败的Toast
                    new Handler(Looper.getMainLooper()).post(() -> {
//                        Toast.makeText(context, "下载失败: " + response.message(), Toast.LENGTH_SHORT).show();
                        callback.onFileReady(null);
                    });
                    throw new IOException("Unexpected code " + response);
                }

                // 将下载内容保存到文件中
                try (InputStream inputStream = response.body().byteStream();
                     FileOutputStream fileOutputStream = new FileOutputStream(file)) {

                    byte[] buffer = new byte[2048];
                    int length;
                    while ((length = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, length);
                    }

                    // 下载完成，回调文件路径
                    new Handler(Looper.getMainLooper()).post(() -> {
//                        Toast.makeText(context, "下载完成: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "路径保存: "+file.getAbsolutePath());
                        callback.onFileReady(file.getAbsolutePath());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



    //    外部启动apk
    public void gotoOtherApp(String packageName,String apkUrl){
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            context.startActivity(launchIntent);
        } else {
            Log.d(TAG, "gotoOtherApp: kkk" );
            // 应用未安装或包名无效
            if(apkUrl.equals("")){
                Toast.makeText(context, Const.MSG_APK_NOT_EXIST + ",请联系管理员添加apk文件", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, Const.MSG_APK_NOT_EXIST, Toast.LENGTH_SHORT).show();
                installApp( apkUrl);
            }

        }
    }

    public void installApp(String apkUrl){
        InstallUtils.checkInstallPermission((Activity) context, new InstallUtils.InstallPermissionCallBack() {
            @Override
            public void onGranted() {
                downloadApp(apkUrl);
            }

            @Override
            public void onDenied() {
                //弹出弹框提醒用户
                AlertDialog alertDialog = new AlertDialog.Builder(context)
                        .setTitle("温馨提示")
                        .setMessage("必须授权才能安装APK，请设置允许安装")
                        .setNegativeButton("取消", null)
                        .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //打开设置页面
                                InstallUtils.openInstallPermissionSetting((Activity) context, new InstallUtils.InstallPermissionCallBack() {
                                    @Override
                                    public void onGranted() {
                                        //去下载Apk
                                        downloadApp(apkUrl);
                                    }

                                    @Override
                                    public void onDenied() {
                                        //还是不允许咋搞？
                                        Toast.makeText(context, "必须授权才能安装APK，请设置允许安装！", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .create();
                alertDialog.show();
            }
        });
    }

    public void downloadApp(String apkUrl){
        showProgressDialog(context, ProgressDialogSetting.download);
//2.下载APK
        InstallUtils.with(context)
                //必须-下载地址
                .setApkUrl(apkUrl)
                //非必须-下载保存的文件的完整路径+/name.apk，使用自定义路径需要获取读写权限
                .setApkPath(DownloadSetting.APK_PATH + "xxx.apk")
                //非必须-下载回调
                .setCallBack(new InstallUtils.DownloadCallBack() {
                    @Override
                    public void onStart() {
                        //下载开始
                    }

                    @Override
                    public void onComplete(String path) {
                        dismissProgressDialog();
                        //下载完成
                        //安装APK
                        /**
                         * 安装APK工具类
                         * @param activity       上下文
                         * @param filePath      文件路径
                         * @param callBack      安装界面成功调起的回调
                         */
                        InstallUtils.installAPK((Activity) context, path, new InstallUtils.InstallCallBack() {
                            @Override
                            public void onSuccess() {
                                //onSuccess：表示系统的安装界面被打开
                                //防止用户取消安装，在这里可以关闭当前应用，以免出现安装被取消
                                Toast.makeText(context, "正在安装程序", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFail(Exception e) {
                                Toast.makeText(context, "安装失败:" + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onLoading(long total, long current) {
                        //下载中
                    }

                    @Override
                    public void onFail(Exception e) {
                        //下载失败
                    }

                    @Override
                    public void cancle() {
                        //下载取消
                    }
                })
                //开始下载
                .startDownload();
    }



}
