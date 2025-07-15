package com.example.api;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MetricsInterceptor implements Interceptor {
    private final MeterRegistry meterRegistry;

    public MetricsInterceptor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String host = request.url().host();
        String method = request.method();

        Counter.builder("http.requests")
                .tag("host", host)
                .tag("method", method)
                .register(meterRegistry)
                .increment();

        long start = System.currentTimeMillis();
        try {
            Response response = chain.proceed(request);
            Timer.builder("http.response.time")
                    .tag("host", host)
                    .tag("method", method)
                    .tag("status", String.valueOf(response.code()))
                    .register(meterRegistry)
                    .record(System.currentTimeMillis() - start, TimeUnit.MILLISECONDS);
            return response;
        } catch (IOException e) {
            Counter.builder("http.errors")
                    .tag("host", host)
                    .tag("method", method)
                    .register(meterRegistry)
                    .increment();
            throw e;
        }
    }
}
