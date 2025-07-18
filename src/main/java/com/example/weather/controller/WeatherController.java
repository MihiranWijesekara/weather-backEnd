package com.example.weather.controller;


import com.example.weather.dto.response.CityWeatherResponse;
import com.example.weather.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/all")
    public ResponseEntity<List<CityWeatherResponse>> getAllCitiesWeather() {
        return ResponseEntity.ok(weatherService.getAllCitiesWeather());
    }
    @GetMapping("/cityID")
    public ResponseEntity<CityWeatherResponse> getWeatherByCityCode(
            @RequestParam String cityCode) {
        return ResponseEntity.ok(weatherService.getWeatherByCityCode(cityCode));
    }
}
