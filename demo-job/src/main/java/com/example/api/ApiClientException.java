package com.example.api;

import lombok.Getter;

@Getter
public class ApiClientException extends RuntimeException {

    private final String requestId;
    private final int statusCode;

    public ApiClientException(String message, String requestId, Throwable cause) {
        super(message, cause);
        this.requestId = requestId;
        this.statusCode = -1;
    }

    public ApiClientException(String message, String requestId, int statusCode) {
        super(message);
        this.requestId = requestId;
        this.statusCode = statusCode;
    }

}
