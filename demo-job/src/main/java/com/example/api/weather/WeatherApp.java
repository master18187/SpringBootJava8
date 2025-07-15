package com.example.api.weather;

import com.example.api.ApiClientException;

public class WeatherApp {
    public static void main(String[] args) {
        // 1. 创建客户端实例
        WeatherApiClient weatherClient = new WeatherApiClient("your_api_key_here");

        // 2. 准备请求
        WeatherRequest request = new WeatherRequest();
        request.setCity("Beijing");
        request.setUnit("metric");
        request.setPrintRequestLog(true);  // 开启请求日志
        request.setPrintResponseLog(true); // 开启响应日志

        try {
            // 3. 执行请求
            WeatherResponse response = weatherClient.requestExecute(request);

            // 4. 使用响应数据
            System.out.printf("当前%s的天气: %.1f°C, %s%n",
                    response.getCity(),
                    response.getTemperature(),
                    response.getDescription());

        } catch (ApiClientException e) {
            System.err.println("获取天气数据失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
