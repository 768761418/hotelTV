package com.qb.hotelTV.Adaptor;

import static com.qb.hotelTV.Utils.LoadUtils.dismissProgressDialog;
import static com.qb.hotelTV.Utils.LoadUtils.showProgressDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.maning.updatelibrary.InstallUtils;
import com.qb.hotelTV.Activity.AppActivity;
import com.qb.hotelTV.Const;
import com.qb.hotelTV.Listener.FocusScaleListener;
import com.qb.hotelTV.Model.ApkModel;
import com.qb.hotelTV.R;
import com.qb.hotelTV.Setting.DownloadSetting;
import com.qb.hotelTV.Setting.ProgressDialogSetting;

import java.util.ArrayList;

public class ApkAdaptor extends RecyclerView.Adapter<ApkAdaptor.ApkViewHolder> {
    Context context;
    ArrayList<ApkModel> apkList;
    FocusScaleListener focusScaleListener = new FocusScaleListener();



    public ApkAdaptor( Context context, ArrayList<ApkModel> apkList) {
        this.context = context;
        this.apkList = apkList;
    }

    @NonNull
    @Override
    public ApkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(this.context).inflate(R.layout.item_apk,parent,false);
        return new ApkAdaptor.ApkViewHolder(view);
    }

    @Override
    @SuppressLint("RecyclerView")
    public void onBindViewHolder(@NonNull ApkViewHolder holder, int position) {
//        holder.apk_logo.setImageBitmap();
        holder.apk_name.setText(apkList.get(position).getName());
        Glide.with(context)
                .load(apkList.get(position).getLogoUrl())
                .error(R.color.white)
                .into(holder.apk_logo);
        if(position == 0){
            holder.apk_item_view.requestFocus();
        }
        holder.apk_item_view.setOnFocusChangeListener(focusScaleListener);
        holder.apk_item_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (apkList.get(position).getApkUrl() == null || apkList.get(position).getApkUrl().equals("")){
                    gotoOtherApp(apkList.get(position).getSchemeUrl(),"");
                }else {
                    gotoOtherApp(apkList.get(position).getSchemeUrl(),apkList.get(position).getApkUrl());
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return apkList.size();
    }


    public static class ApkViewHolder extends RecyclerView.ViewHolder {
        TextView apk_name;
        ImageView apk_logo;
        LinearLayout apk_item_view;

        public ApkViewHolder(@NonNull View itemView) {
            super(itemView);
            apk_logo = itemView.findViewById(R.id.apk_logo);
            apk_name = itemView.findViewById(R.id.apk_name);
            apk_item_view = itemView.findViewById(R.id.apk_item_view);
        }
    }


    //    外部启动apk
    private void gotoOtherApp(String packageName,String apkUrl){

        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if (launchIntent != null) {
            context.startActivity(launchIntent);
        } else {
            // 应用未安装或包名无效
            if(apkUrl.equals("")){
                Toast.makeText(context, Const.MSG_APK_NOT_EXIST + ",请联系管理员添加apk文件", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(context, Const.MSG_APK_NOT_EXIST, Toast.LENGTH_SHORT).show();
                installApp( apkUrl);
            }

        }
    }
    private void installApp(String apkUrl){
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

    private void downloadApp(String apkUrl){
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
