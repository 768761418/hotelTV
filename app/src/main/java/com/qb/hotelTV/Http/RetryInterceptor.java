package com.qb.hotelTV.Http;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class RetryInterceptor implements Interceptor {

    // 最大重试次数
    private int maxRetries;
    // 当前重试次数
    private int retryCount = 0;

    // 构造方法，接受最大重试次数作为参数
    public RetryInterceptor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        // 获取当前请求
        Request request = chain.request();
        Response response = null;
        IOException exception = null;

        // 在达到最大重试次数前，不断尝试发送请求
        while (retryCount < maxRetries) {
            try {
                // 尝试执行请求
                response = chain.proceed(request);
                // 如果请求成功，直接返回响应
                if (response.isSuccessful()) {
                    return response;
                }
            } catch (IOException e) {
                // 捕获异常，记录并增加重试次数
                exception = e;
                retryCount++;
            }
        }

        // 如果超出最大重试次数仍然没有成功
        if (exception != null) {
            // 抛出最后一次捕获的异常
            throw exception;
        }

        // 返回最后一次的响应
        return response;
    }
}
