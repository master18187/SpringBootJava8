package com.example.api;

public interface ApiClient<Req extends HttpRequestBean, Resp extends HttpResponseBean> {

    Resp requestExecute(Req req);

}
