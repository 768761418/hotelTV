package com.qb.hotelTV.api;

import android.app.Activity;
import android.content.Context;
import android.util.Log;



import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.text.ParseException;

import javax.net.ssl.SSLHandshakeException;

import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

//统一的response预处理
public class OnResponseIntercepter extends DisposableObserver<ResponseBody> {
    private static final String TAG = "OnResponseIntercepter";
    private ResponsePretreatListener responsePretreatListener; //请求处理的监听器
    private Context context;

    @Override
    public void onNext(ResponseBody responseBody) {
        Log.i(TAG, "onNext");
        //成功接收到有效的数据,并返回给调用者

        String result = "";
        try {
            result = responseBody.string();
            responsePretreatListener.onSuccess(result);
        } catch (Exception e) {
//            ErrorLogUtils.getprintStackInfo(e);
            e.printStackTrace();
        }
        if (context != null) {
            Log.i(TAG, "请求api完毕");
            if (context instanceof Activity) {
//                if (!((Activity) context).isFinishing())
//                    AlertUtils.loadDissmiss();
            } else {
//                AlertUtils.loadDissmiss();
            }
        }
    }

    public OnResponseIntercepter(ResponsePretreatListener responseToast, Context context) {
        this.responsePretreatListener = responseToast;
        this.context = context;
    }

    /**
     * 对错误进行统一处理
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if (context != null) {
            Log.i(TAG, "请求api完毕");
//            if (context instanceof Activity) {
//                if (!((Activity) context).isFinishing())
//                    AlertUtils.loadDissmiss();
//            } else {
//                AlertUtils.loadDissmiss();
//            }
        }
        if (e instanceof SocketTimeoutException) {
            Log.i(TAG, "SocketTimeoutException");
            responsePretreatListener.onfailed("Network connection timeout", 400);//网络连接超时
//            ErrorLogUtils.setLog(e.toString());
//
//            AlertUtils.toastAlert("Network connection timeout : 400");
        }//请求超时
        else if (e instanceof ConnectException) {
            Log.i(TAG, "网络连接超时");
            responsePretreatListener.onfailed("Network connection timeout", 400);//网络连接超时
//            ErrorLogUtils.setLog(e.toString());
//            AlertUtils.toastAlert("Network connection timeout : 400");
        } else if (e instanceof SSLHandshakeException) {
            Log.i(TAG, "SSL证书异常");
            responsePretreatListener.onfailed("Ssl certificate is abnormal", 403);
//            ErrorLogUtils.setLog(e.toString());
//            AlertUtils.toastAlert("Ssl certificate is abnormal : 400");
        } else if (e instanceof HttpException) {
            //获取错误码
            int code = ((HttpException) e).code();
            String msg = null;
            try {
                //获取错误的response信息
                msg = ((HttpException) e).response().errorBody().string();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
//            AlertUtils.toastAlert(code + ":" + msg);

            Log.i(TAG, "code=" + code);
//            if (code >= 500) {
//                responsePretreatListener.onfailed(msg, code);
//            } else if (code == 500) {
//                ErrorDto errorDto = GsonUtils.jsonToObject(msg, ErrorDto.class);
//                if (errorDto != null) {
//                    responsePretreatListener.onfailed(errorDto.getMessage(), code);
//                    AlertUtils.toastAlert(errorDto.getMessage() + " " + "(" + code + ")");
//                } else {
//                    responsePretreatListener.onfailed(msg, code);
//                }
//            } else if (code == 404) {
//                responsePretreatListener.onfailed(msg, code);
//            } else if (code == 601 || code == 600) {
//                //包括了token过期，sso未登录，
//                responsePretreatListener.onfailed(msg, code);
//                // 清空登陆信息
////                Mma4_0_ConfigModel.getInstance().cleanSsoLoginInfo();
////                Mma4_0_ConfigModel.getInstance().clearSsoCookies();
////                AppUtils.exitUser(MyApplication.getContext());
//
////            } else if (code == 827) {
////                responsePretreatListener.onfailed(MyApplication.getContext().getString(R.string.user_is_disabled), code);
////            } else {
////                responsePretreatListener.onfailed(msg, code);
////            }
//            String temp = "error : code =" + code + "; msg = " + msg;
////            ErrorLogUtils.setLog(temp);

        } else if (e instanceof UnknownHostException) {
            Log.i(TAG, "域名解析失败");
            responsePretreatListener.onfailed("Domain access failed", 400);
//            ErrorLogUtils.setLog(e.toString());

        } else {
            //其他未知类型的错误
            Log.i(TAG, "other" + e.getMessage());
            responsePretreatListener.onfailed(e.getMessage(), 400);
//            ErrorLogUtils.setLog(e.toString());

        }
    }

    //TODO 如果需要在请求结束的时候实现额外的操作，可以在该函数里面实现
    @Override
    public void onComplete() {
        //TODO 请求成功之后，需要隐藏加载框，在这里，自己实现

    }

    //TODO 如果需要在请求开始的时候实现额外的操作，可以在该函数里面实现
    @Override
    protected void onStart() {
        super.onStart();
        //TODO 需要显示加载框的，看这里，自己实现
        if (context == null) return;
//        if (!((Activity) context).isFinishing()){
//            AlertUtils.getLoadingDialogInstance(context).show();
//        }
    }

    public interface ResponsePretreatListener {
        void onfailed(String result, int code);
        void onSuccess(String result) throws ParseException;
    }

}

