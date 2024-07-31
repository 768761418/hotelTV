package com.qb.hotelTV.Utils;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

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
                    Toast.makeText(context, "下载失败", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // 在主线程上显示下载失败的Toast
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(context, "下载失败: " + response.message(), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, "下载完成: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        Log.d(TAG, "路径保存: "+file.getAbsolutePath());
                        callback.onFileReady(file.getAbsolutePath());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }



}
