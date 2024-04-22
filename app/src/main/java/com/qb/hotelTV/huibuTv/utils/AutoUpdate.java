package com.qb.hotelTV.huibuTv.utils;

import static org.webrtc.ContextUtils.getApplicationContext;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;


import com.qb.hotelTV.huibuTv.MyApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AutoUpdate {
    private static final String TAG = "AutoUpdate";
//    检查更新并在线升级
    public static boolean checkUpdate() {
//       在子线程中向服务器请求最新版本号
        //        在子线程中进行网络请求
        Thread thread= new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String url = "http://"+ MyApplication.getHostIP()+":80/version/version/getNewVersion";
                    OkHttpClient client = new OkHttpClient().newBuilder()
                            .build();
                    MediaType mediaType = MediaType.parse("text/plain");
                    RequestBody body = RequestBody.create(mediaType, "");
                    Request request = new Request.Builder()
                            .url(url)
                            .method("POST", body)
                            .build();
                    Response response = client.newCall(request).execute();
                    // 解析获取返回的data字段
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    MyApplication.setUpdateData(jsonObject.getString("data"));
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
        if(MyApplication.getUpdateData() == null) {
            return false;
        }
        return true;
    }

    public static void showUpdateDialog(Context context) {
        // 解析data字段
        try {
            JSONObject jsonObject1 = new JSONObject(MyApplication.getUpdateData());
            // 将获取到的版本号和当前版本号进行比较，如果服务器版本号大于当前版本号，则进行在线升级
            if (jsonObject1.getInt("versionCode") > MyApplication.getVersionCode(getApplicationContext())) {
                // 弹出对话框，提示用户更新
                AlertDialog.Builder builder = new AlertDialog.Builder(context,com.google.android.material.R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                builder.setTitle("发现新版本"+jsonObject1.getString("versionName"));
                builder.setMessage(jsonObject1.getString("versionComment"));
                builder.setCancelable(false);
                String downloadUrl = "http://"+ MyApplication.getHostIP()+":80/version/version/download?resource="+jsonObject1.getString("versionFile");
                builder.setPositiveButton("确定", (dialog, which) -> {
//                  下载apk的路径
                    String apkPath = getApplicationContext().getExternalFilesDir("apk").getAbsolutePath();
                    // 使用okhttp下载apk
                    DownloadUtil.get().download(downloadUrl, apkPath,"huiBuTv.apk", new DownloadUtil.OnDownloadListener() {
                        @Override
                        public void onDownloadSuccess(File file) {
                            // 下载成功后，调用系统的安装方法
                            InstallUtil.installApk(getApplicationContext(), file);
//                            结束当前进程
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }

                        @Override
                        public void onDownloading(int progress) {
                            // 下载中，更新进度条
                        }

                        @Override
                        public void onDownloadFailed(Exception e) {
//                            下载失败,提示用户并结束应用
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context,com.google.android.material.R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                            builder1.setTitle("下载失败");
                            builder1.setMessage("请检查网络连接");
                            builder1.setCancelable(false);
                            builder1.setPositiveButton("确定", (dialog1, which1) -> {
                                android.os.Process.killProcess(android.os.Process.myPid());
                            });
                            builder1.show();

                        }
                    });
                    // 跳转到下载页面
                });
                // 强制更新则不允许取消
                if(jsonObject1.getInt("versionForce") == 1) {
                    builder.setCancelable(false);
                    builder.setTitle("发现新版本"+jsonObject1.getString("versionName")+"，请更新后继续使用");
                } else {
                    builder.setNegativeButton("取消", (dialog, which) -> {
                        dialog.dismiss();
                    });
                }
                builder.show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
//            if (jsonObject1.getInt("versionCode") > MyApplication.getVersionCode( getApplicationContext())) {
//                    // 进行在线升级
//                    String downloadUrl = "http://"+ MyApplication.getHostIP()+":80/version/version/download?resource="+jsonObject1.getString("versionFile");
////                        打印下载地址
//                    System.out.println(downloadUrl);
////                        弹出升级dialog
//                        AlertDialog.Builder builder = new AlertDialog.Builder(context,com.google.android.material.R.style.Theme_AppCompat_DayNight_Dialog_Alert);
//                        builder.setTitle("更新提示");
//                        builder.setMessage("检测到新版本，是否更新？");
//                        builder.setPositiveButton("确定", (dialog, which) -> {
////                            下载apk的路径
//                            String apkPath = getApplicationContext().getExternalFilesDir("apk").getAbsolutePath();
//                            // 使用okhttp下载apk
//                            DownloadUtil.get().download(downloadUrl, apkPath,"huiBuTv.apk", new DownloadUtil.OnDownloadListener() {
//                                @Override
//                                public void onDownloadSuccess(File file) {
//                                    // 下载成功后，调用系统的安装方法
//                                    InstallUtil.installApk(getApplicationContext(), file);
//                                }
//
//                                @Override
//                                public void onDownloading(int progress) {
//                                    // 下载中
//                                }
//
//                                @Override
//                                public void onDownloadFailed(Exception e) {
//
//                                }
//
//                            });
//
//                        });
//                        // 强制更新则不允许取消
//                        if(jsonObject1.getInt("versionForce") == 1) {
//                            builder.setCancelable(false);
//                        } else {
//                            builder.setNegativeButton("取消", (dialog, which) -> {
//                                dialog.dismiss();
//                            });
//                        }
//                        builder.show();

                    //第二种方式，使用MODEL方式，组装好对应的MODEL，传入sdk中
//                        DownloadInfo info = new DownloadInfo().setApkUrl(downloadUrl)
//                                .setFileSize(jsonObject1.getLong("versionSize"))
//                                .setProdVersionCode(jsonObject1.getInt("versionCode"))
//                                .setProdVersionName(jsonObject1.getString("versionName"))
//                                .setMd5Check(jsonObject1.getString("versionMd5"))
//                                .setForceUpdateFlag(jsonObject1.getInt("versionForce"))
//                                .setUpdateLog(jsonObject1.getString("versionComment"));
//                        AppUpdateUtils.getInstance()
//        //                        .addMd5CheckListener(...)//添加MD5检查更新
//        //                        .addAppDownloadListener(...)//添加文件下载监听
//                                .checkUpdate(info);
//                    }
//                    });