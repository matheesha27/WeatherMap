package com.weatherapp.WeatherApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenWeatherMapResponse {

    private Object coord;
    private List<Object> weather;
    private String base;
    private MainParams main;
    private int visibility;
    private Object wind;
    private Object clouds;
    private long dt;
    private Object sys;
    private float timezone;
    private long id;
    private String name;
    private int cod;
    private String message;
}
