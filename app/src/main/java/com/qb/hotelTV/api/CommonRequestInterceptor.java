package com.qb.hotelTV.api;

import android.util.Log;

import androidx.annotation.NonNull;

import com.qb.hotelTV.Activity.CommonActivity.StartupActivity;
import com.qb.hotelTV.Http.BackstageHttp;
import com.qb.hotelTV.Utils.SharedPreferencesUtils;
import com.qb.hotelTV.huibuTv.MyApplication;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CommonRequestInterceptor implements Interceptor {
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Log.d("intercept", "intercept: "+chain.request().url());
        Request.Builder builder = chain.request().newBuilder();
        String tenant_id = SharedPreferencesUtils.getInstance(MyApplication.getContext()).loadTenant();
        builder.addHeader("tenant-id", tenant_id);
        builder.addHeader("Authorization", "Bearer "+SharedPreferencesUtils.getInstance(MyApplication.getContext()).loadToken());
        return chain.proceed(builder.build());
    }
}
