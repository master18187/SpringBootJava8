package com.example.api.weather;

import com.example.api.HttpResponseBean;
import lombok.Data;

@Data
public class WeatherResponse extends HttpResponseBean {
    private String city;
    private double temperature;
    private String unit;
    private String description;
}
