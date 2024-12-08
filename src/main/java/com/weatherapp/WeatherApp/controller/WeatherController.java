package com.weatherapp.WeatherApp.controller;

import com.weatherapp.WeatherApp.dto.ErrorResponse;
import com.weatherapp.WeatherApp.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@RestController
public class WeatherController {

    @Autowired
    private WeatherService weatherService;

    private Logger logger = LoggerFactory.getLogger("common-log");

    @GetMapping("/weather")
    public Object getWeatherStatus(@RequestParam Map<String, String> params) {
        try {
            if (params.containsKey("city") && params.get("city") != null && !Objects.equals(params.get("city"), "")) {
                String city = params.get("city");
                logger.info("Request Received for City: {}", city);
                return weatherService.getWeatherStatus(city);

            } else if (!params.containsKey("city")) {
                logger.error("Required request parameter 'city' for method parameter type String is not present");
                ErrorResponse errorResponse = new ErrorResponse(new Date(),
                        400,
                        "Required request parameter 'city' for method parameter type String is not present",
                        "/weather");
                return errorResponse;

            } else if (params.get("city") == null | Objects.equals(params.get("city"), "")) {
                logger.error("Nothing to geocode. Required request parameter value 'city' is null or empty");
                ErrorResponse errorResponse = new ErrorResponse(new Date(),
                        400,
                        "Nothing to geocode. Required request parameter 'city' is null or empty",
                        "/weather");
                return errorResponse;
            }
        } catch (Exception e) {
            return e.toString();
        }
        return null;
    }
}