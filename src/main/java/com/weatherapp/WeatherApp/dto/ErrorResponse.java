package com.weatherapp.WeatherApp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {

    @JsonProperty("Timestamp")
    private Date timestamp;

    @JsonProperty("Status Code")
    private int statusCode;

    @JsonProperty("Error Message")
    private String errorMessage;

    @JsonProperty("Endpoint")
    private String path;
}
