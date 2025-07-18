package com.example.weather.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class CityWeatherResponse {
    private String cityCode;
    private String cityName;
    private double temperature;
    private double temperatureMin;
    private double temperatureMax;
    private String status;
    private String formattedTemperature;
    private String formattedSunrise;
    private String formattedSunset;
    private double pressure;
    private double humidity;
    private int timezone;
    private int visibility;
    private double windSpeed;
    private double windDeg;

    public CityWeatherResponse(String cityCode, String cityName,
                               double temperature, String status,
                               double temperatureMin, double temperatureMax,
                               long sunrise, long sunset,
                               double pressure, double humidity,int timezone,int visibility,double windSpeed,double windDeg) {
        this.cityCode = cityCode;
        this.cityName = cityName;
        this.temperature = temperature;
        this.status = status;
        this.temperatureMin = temperatureMin;
        this.temperatureMax = temperatureMax;
        this.pressure = pressure;
        this.humidity = humidity;
        this.formattedTemperature = String.format("%.1f°C", temperature);
        this.formattedSunrise = formatUnixTime(sunrise);
        this.formattedSunset = formatUnixTime(sunset);
        this.timezone = timezone;
        this.visibility = visibility;
        this.windSpeed = windSpeed;
        this.windDeg = windDeg;
    }

    private String formatUnixTime(long unixTime) {
        return Instant.ofEpochSecond(unixTime)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("HH:mm"));
    }
}