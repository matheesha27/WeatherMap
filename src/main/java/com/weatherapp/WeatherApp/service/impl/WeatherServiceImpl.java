package com.weatherapp.WeatherApp.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.weatherapp.WeatherApp.dto.ErrorStatusOutput;
import com.weatherapp.WeatherApp.dto.OpenWeatherMapErrorResponse;
import com.weatherapp.WeatherApp.dto.OpenWeatherMapResponse;
import com.weatherapp.WeatherApp.dto.WeatherStatusOutput;
import com.weatherapp.WeatherApp.service.WeatherService;
import com.weatherapp.WeatherApp.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    @Value("${weather.api.key}")
    private String weatherApiKey;

    @Autowired
    private DateUtil dateUtil;

    private Logger logger = LoggerFactory.getLogger("common-log");

    @Autowired
    private ObjectMapper objectMapper;

    private final ConcurrentHashMap<String, Float> currentTempMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Float> minTempMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Float> maxTempMap = new ConcurrentHashMap<>();

    public Object getWeatherStatus(String city) {
        URL url = null;
        try {
            url = new URL(weatherApiUrl.concat("weather?q=")
                    .concat(city)
                    .concat("&APPID=")
                    .concat(weatherApiKey));
            System.out.println(url);

            HttpClient client = HttpClient.newHttpClient();
            // Build the GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(url.toURI())
                    .GET()
                    .build();
            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Handle response
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                logger.info("SUCCESS - Status Code: {}, Response Body: {}", response.statusCode(), response.body());

                // Convert JSON to Java object
                OpenWeatherMapResponse openWeatherMapResponse = objectMapper.readValue(response.body(), OpenWeatherMapResponse.class);
                System.out.println(openWeatherMapResponse.toString());

                this.putTempEntries(openWeatherMapResponse);

                WeatherStatusOutput weatherStatusOutput = new WeatherStatusOutput();
                weatherStatusOutput.setCity(openWeatherMapResponse.getName());
                weatherStatusOutput.setAverageTemperature(getAverageTemperature(openWeatherMapResponse.getName()));
                weatherStatusOutput.setColdestDay(getColdestHottestDays(openWeatherMapResponse.getName()).get(0));
                weatherStatusOutput.setHottestDay(getColdestHottestDays(openWeatherMapResponse.getName()).get(1));
                return weatherStatusOutput;

            } else if (response.statusCode() >= 400 && response.statusCode() < 500) {
                logger.error("Client error with Status Code: {}, Response Body: {}", response.statusCode(), response.body());
                return this.generateErrorResponse(response);

            } else if (response.statusCode() >= 500) {
                logger.error("Server error: with Status Code: {}, Response Body: {}", response.statusCode(), response.body());
                return this.generateErrorResponse(response);
            }

        } catch (URISyntaxException | IOException | InterruptedException  e) {
            logger.error("Error in creating the URL: {}", e.getMessage());
        }
        return null;
    }

    private ErrorStatusOutput generateErrorResponse(HttpResponse<String> response) {
        OpenWeatherMapErrorResponse openWeatherMapErrorResponse = null;
        try {
            openWeatherMapErrorResponse = objectMapper.readValue(response.body(), OpenWeatherMapErrorResponse.class);
        } catch (JsonProcessingException e) {
            logger.error("JSON processing error: {}", e.getMessage());
        }
        ErrorStatusOutput errorStatusOutput = new ErrorStatusOutput();
        errorStatusOutput.setStatusCode(openWeatherMapErrorResponse.getCod());
        errorStatusOutput.setErrorMessage(openWeatherMapErrorResponse.getMessage());
        System.out.println(errorStatusOutput);
        return errorStatusOutput;
    }

    private void putTempEntries(OpenWeatherMapResponse openWeatherMapResponse) {
        String cityNameDateKey = openWeatherMapResponse.getName().concat("|").concat(dateUtil.getServerTime());
        //Current Temp Entry
        this.currentTempMap.put(cityNameDateKey, openWeatherMapResponse.getMain().getTemp());
        //Coldest Day Entry
        this.minTempMap.put(cityNameDateKey, openWeatherMapResponse.getMain().getTemp_min());
        //Hottest Day Entry
        this.maxTempMap.put(cityNameDateKey, openWeatherMapResponse.getMain().getTemp_max());
    }

    private List<String> getLast7Days() {
        List<String> last7Days = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            last7Days.add(LocalDate.now().minusDays(i).toString());
        }
        return last7Days;
    }

    private float getAverageTemperature(String cityName) {
        float sum = 0;
        int count = 0;
        List<String> last7Days = this.getLast7Days();
        for (String key : this.currentTempMap.keySet()) {
            String cityNameKey = key.split("\\|")[0];
            String dateKey = key.split("\\|")[1];
            float tempVal = currentTempMap.get(key);
            if (cityNameKey.equals(cityName) && last7Days.contains(dateKey)) {
                sum += tempVal;
                count += 1;
            }
        }
        float avgTemp = sum / count;
        logger.info("Avg. temp of city: {} = {}",cityName, avgTemp);
        return avgTemp;
    }

    private List<String> getColdestHottestDays(String cityName) {
        List<String> last7Days = this.getLast7Days();
        String today = dateUtil.getServerTime();
        float minTempVal = 0;
        float maxTempVal = 0;
        List<String> marginDays = new ArrayList<>(List.of(today, today));
        for (String key : this.minTempMap.keySet()) {
            String cityNameKey = key.split("\\|")[0];
            String dateKey = key.split("\\|")[1];
            if (cityNameKey.equals(cityName) && last7Days.contains(dateKey)
                    && this.minTempMap.get(key) != null && minTempMap.get(key) < minTempVal) {
                minTempVal = minTempMap.get(key);
                marginDays.set(0, dateKey);
            }
        }
        for (String key : this.maxTempMap.keySet()) {
            String cityNameKey = key.split("\\|")[0];
            String dateKey = key.split("\\|")[1];
            if (cityNameKey.equals(cityName) && last7Days.contains(dateKey)
                    && this.minTempMap.get(key) != null && maxTempMap.get(key) >= maxTempVal) {
                maxTempVal = maxTempMap.get(key);
                marginDays.set(1, dateKey);
            }
        }
        logger.info("Margin Days: {}", marginDays);
        return marginDays;
    }
}
