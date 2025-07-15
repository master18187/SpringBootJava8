package com.example.api;

import lombok.Data;

import java.util.Map;

@Data
public class HttpRequestBean {

    private String requestId;

    private String method;

    private boolean printRequestLog = true;

    private boolean printResponseLog = true;

    private boolean saveRequestLog = false;

    private boolean asyncSaveRequestLog = true;

    private String url;

    private String requestBody;

    private Map<String, Object> headers;

}
