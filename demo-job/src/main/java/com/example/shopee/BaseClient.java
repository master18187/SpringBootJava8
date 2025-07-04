package com.example.shopee;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public abstract class BaseClient<Req extends HttpRequestBean, Resp extends HttpResponseBean> implements ApiClient<Req, Resp> {

    @Override
    public Resp requestExecute(Req req){
        Resp resp = (Resp) new HttpResponseBean();
        try {

            String requestId = createRequestId();
            req.setRequestId(requestId);

            if (req.isPrintRequestLog()) {
                log.info("request execute url: {}, requestId: {}, requestBody: {}, headers: {}", req.getUrl(), requestId, req.getRequestBody(), req.getHeaders());
            }


            String responseBody = requestExecuteInternal(req);

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
