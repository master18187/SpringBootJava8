package com.example.shopee;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.util.Map;
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
                    sharedClient = new OkHttpClient.Builder()
                            .connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES))
                            // .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                            .hostnameVerifier((hostname, session) -> true) // 跳过主机名验证
                            .build();
                }
            }
        }

        return buildClient(sharedClient);
    }

    protected OkHttpClient buildClient(OkHttpClient client) {
        return client.newBuilder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                // .addInterceptor(logging)
                .build();
    }

    @Override
    public Resp requestExecute(Req req) {
        Resp resp = (Resp) new HttpResponseBean();
        try {

            String requestId = createRequestId();
            req.setRequestId(requestId);

            if (req.isPrintRequestLog()) {
                log.info("request execute url: {}, requestId: {}, requestBody: {}, headers: {}", req.getUrl(), requestId, req.getRequestBody(), req.getHeaders());
            }

            OkHttpClient client = initAndGetHttpClient();

            client = buildClient(client);

            HttpUrl.Builder urlBuilder = HttpUrl.parse(req.getUrl()).newBuilder();

            Request.Builder requestBuilder = new Request.Builder()
                    .url(urlBuilder.build())
                    .header("requestId", requestId)
                    .get();


            Request request = requestBuilder.build();
            String responseBody = requestExecuteInternal(req);

            try (Response response = client.newCall(request).execute()) {
                System.out.println(response.body().string());
            }

            if (req.isPrintResponseLog()) {
                log.info("request execute responseBody: {}", responseBody);
            }

        } catch (Exception e) {
            // 异常日志处理
            if (req.isSaveRequestLog() && req.isAsyncSaveRequestLog()) {
                //
                saveRequestLog(null);
            } else if (req.isSaveRequestLog() && !req.isAsyncSaveRequestLog()) {

            }
        }
        return resp;
    }

    protected String createRequestId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    protected void saveRequestLog(RequestLog requestLog) {

    }

    public abstract String buildAuthParams(Req req);

    public abstract String initAndPrepareParams(Req req);

    public abstract String requestExecuteInternal(Req req);

}
