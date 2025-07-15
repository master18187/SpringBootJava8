package com.example.api.weather;

import com.example.api.BaseClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class WeatherApiClient extends BaseClient<WeatherRequest, WeatherResponse> {
    private final String apiKey;

    public WeatherApiClient(String apiKey) {
        this.apiKey = apiKey;
        this.connectTimeout = 10L; // 10秒连接超时
        this.readTimeout = 30L;    // 30秒读取超时
    }

    @Override
    protected void addAuthHeaders(Request.Builder builder, WeatherRequest req) {
        // 添加API Key认证
        builder.header("X-API-KEY", apiKey);
    }

    @Override
    protected WeatherResponse handleResponse(Response response, WeatherRequest req) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response);
        }

        String responseBody = response.body().string();
        WeatherResponse weatherResp = new WeatherResponse();

        // 简单解析JSON响应
        JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
        weatherResp.setCity(json.get("name").getAsString());
        weatherResp.setTemperature(json.getAsJsonObject("main").get("temp").getAsDouble());
        weatherResp.setDescription(json.getAsJsonArray("weather")
                .get(0).getAsJsonObject()
                .get("description").getAsString());

        return weatherResp;
    }

    @Override
    protected Request buildRequest(WeatherRequest req) {
        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather")
                .newBuilder()
                .addQueryParameter("q", req.getCity())
                .addQueryParameter("units", req.getUnit())
                .build();

        return new Request.Builder()
                .url(url)
                .get()
                .build();
    }
}
