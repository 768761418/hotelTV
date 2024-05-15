package com.qb.hotelTV.Utils;

import android.app.ProgressDialog;
import android.content.Context;

public class LoadUtils {
    private static ProgressDialog progressDialog;

    // 显示等待对话框
    public static void showProgressDialog(Context context,String text) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(text); // 设置等待消息
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // 设置进度条样式为圆形旋转
        progressDialog.setCancelable(false); // 设置对话框不可取消
        progressDialog.show(); // 显示对话框
    }

    // 隐藏等待对话框
    public static void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss(); // 关闭对话框
        }
    }
}
