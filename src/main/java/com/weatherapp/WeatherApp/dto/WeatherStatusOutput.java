package com.weatherapp.WeatherApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherStatusOutput {

    private String city;
    private float averageTemperature;
    private String hottestDay;
    private String coldestDay;
}
