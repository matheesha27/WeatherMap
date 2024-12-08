package com.weatherapp.WeatherApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CityWeatherHistory {

    private String cityName;
    private HashMap<String, Float> minTempMap;
    private HashMap<String, Float> maxTempMap;
}
