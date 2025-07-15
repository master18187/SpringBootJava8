package com.example.api;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

@Slf4j
public class LoggingInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTime = System.nanoTime();

        log.info("Sending request: {} {} with headers {}",
                request.method(), request.url(), request.headers());

        Response response = chain.proceed(request);

        long duration = (System.nanoTime() - startTime) / 1_000_000;

        log.info("Received response for {} {} in {}ms with code {}",
                request.method(), request.url(), duration, response.code());

        return response;
    }
}
