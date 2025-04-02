package com.example.common.context;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashMap;

@EqualsAndHashCode(callSuper = true)
@Data
public class Profile extends LinkedHashMap<String, Object> {

    private int appId;

    private String appName;

    private int userId;

    private String userName;

    private String traceId;

    private String spanId;
}
