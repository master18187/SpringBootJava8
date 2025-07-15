package com.example.api;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class BaseClient<Req extends HttpRequestBean, Resp extends HttpResponseBean> implements ApiClient<Req, Resp> {

    protected Long connectTimeout = 60L;
    protected Long readTimeout = 60L;
    protected Long writeTimeout = 60L;

    private static volatile OkHttpClient sharedClient = null;
    private static final Object LOCK = new Object();

    protected OkHttpClient initAndGetHttpClient() {
        if (sharedClient == null) {
            synchronized (LOCK) {
                if (sharedClient == null) {
                    sharedClient = createDefaultClient();
                }
            }
        }
        return buildClient(sharedClient);
    }


    protected OkHttpClient createDefaultClient() {
        return new OkHttpClient.Builder()
                .connectionPool(new ConnectionPool(10, 5, TimeUnit.MINUTES))
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    protected OkHttpClient buildClient(OkHttpClient client) {
        return client.newBuilder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public Resp requestExecute(Req req) {
        try {
            String requestId = createRequestId();
            req.setRequestId(requestId);

            // 记录请求日志
            if (req.isPrintRequestLog()) {
                log.info("Request: {} {} | ID: {} | Headers: {} | Body: {}",
                        req.getMethod(), req.getUrl(), requestId,
                        req.getHeaders(), req.getRequestBody());
            }

            // 构建请求
            Request request = buildRequest(req);

            // 执行请求
            OkHttpClient client = initAndGetHttpClient();
            try (Response response = client.newCall(request).execute()) {
                Resp resp = handleResponse(response, req);

                // 记录响应日志
                if (req.isPrintResponseLog()) {
                    log.info("Response for ID {}: {}", requestId, resp);
                }

                return resp;
            }
        } catch (IOException e) {
            log.error("Request failed for ID {}", req.getRequestId(), e);
            throw new ApiClientException("Request failed", req.getRequestId(), e);
        }
    }

    protected Request buildRequest(Req req) {
        Request.Builder builder = new Request.Builder()
                .url(req.getUrl())
                .header("requestId", req.getRequestId());

        // 添加认证信息
        addAuthHeaders(builder, req);

        // 设置请求方法
        switch (req.getMethod()) {
            case "GET":
                builder.get();
                break;
            case "POST":
                builder.post(RequestBody.create(req.getRequestBody(), MediaType.get("application/json")));
                break;

        }

        return builder.build();
    }

    protected abstract void addAuthHeaders(Request.Builder builder, Req req);

    protected abstract Resp handleResponse(Response response, Req req) throws IOException;

    protected String createRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }


}
