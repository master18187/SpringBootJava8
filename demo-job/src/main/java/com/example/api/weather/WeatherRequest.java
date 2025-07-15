package com.example.api.weather;

import com.example.api.HttpRequestBean;
import lombok.Data;

@Data
public class WeatherRequest extends HttpRequestBean {
    private String city;
    private String unit; // "metric" or "imperial"
}
