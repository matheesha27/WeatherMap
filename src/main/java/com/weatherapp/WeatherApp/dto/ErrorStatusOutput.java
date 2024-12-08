package com.weatherapp.WeatherApp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorStatusOutput {

    @JsonProperty("Status Code")
    private int statusCode;

    @JsonProperty("Error Message")
    private String errorMessage;
}
