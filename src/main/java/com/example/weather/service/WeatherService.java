package com.example.weather.service;

import com.example.weather.dto.response.CityWeatherResponse;

import java.util.List;

public interface  WeatherService {
    List<CityWeatherResponse> getAllCitiesWeather();
    CityWeatherResponse getWeatherByCityCode(String cityCode);
}
