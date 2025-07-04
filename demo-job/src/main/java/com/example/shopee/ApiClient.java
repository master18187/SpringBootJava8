package com.example.shopee;

public interface ApiClient<Req extends HttpRequestBean, Resp extends HttpResponseBean> {

    default void beforeRequestExecute(Req req) {

    }

    Resp requestExecute(Req req);

    default void afterRequestExecute(Req req, Resp resp) {

    }

}
