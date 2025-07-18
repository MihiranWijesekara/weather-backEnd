package com.example.weather.service.impl;

import com.example.weather.dto.response.CityWeatherResponse;
import com.example.weather.service.WeatherService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class WeatherServiceImpl implements WeatherService {

    @Data
    @AllArgsConstructor
    private static class CityData {
        private String cityCode;
        private String cityName;
    }
    private final List<CityData> cities = List.of(
            new CityData("1248991", "Colombo"),
            new CityData("1850147", "Tokyo"),
            new CityData("2644210", "Liverpool"),
            new CityData("2988507", "Paris"),
            new CityData("2147714", "Sydney")

    );
    private  final String apiKey  = "b27f1596a05b5bb3205c70ce5bc0fa9a";
    private final String apiUrl  = "https://api.openweathermap.org/data/2.5/weather";

    @Override
    public List<CityWeatherResponse> getAllCitiesWeather() {
        List<CityWeatherResponse> responses = new ArrayList<>();
        for (CityData city : cities) {
            try {
                CityWeatherResponse response = fetchWeatherFromApi(city);
                responses.add(response);
            } catch (Exception e) {
                System.err.println("Error fetching weather for city: " + city.getCityName() + " - " + e.getMessage());

                responses.add(new CityWeatherResponse(
                        city.getCityCode(),
                        city.getCityName(),
                        0.0,
                        "Weather data unavailable",
                        0.0,
                        0.0,
                0L,
                        0L,
                        0.0,
                        0.0,
                        0
                ));
            }
        }
        return responses;
    }

    @Override
    public CityWeatherResponse getWeatherByCityCode(String cityCode) {
        Optional<CityData> cityData = cities.stream()
                .filter(city -> city.getCityCode().equals(cityCode))
                .findFirst();

        if (cityData.isEmpty()) {
            throw new RuntimeException("City not found with code: " + cityCode);
        }

        try {
            return fetchWeatherFromApi(cityData.get());
        } catch (Exception e) {
            System.err.println("Error fetching weather for city: " + cityData.get().getCityName() + " - " + e.getMessage());
            return new CityWeatherResponse(
                    cityCode,
                    cityData.get().getCityName(),
                    0.0,
                    "Weather data unavailable",
                    0.0,
                    0.0,
                    0L,
                    0L,
                    0.0,
                    0.0,
                    0
            );
        }
    }


    private CityWeatherResponse fetchWeatherFromApi(CityData city) {
        String url = String.format("%s?id=%s&appid=%s&units=metric",
                apiUrl, city.getCityCode(), apiKey);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);

        if(response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("API request failed for city: " + city.getCityName());
        }

        Map<String, Object> body = response.getBody();
        Map<String, Object> main = (Map<String, Object>) body.get("main");
        Map<String, Object> sys = (Map<String, Object>) body.get("sys");

        int timezone = (int) body.get("timezone");

        double temp = Double.parseDouble(main.get("temp").toString());
        double tempMin = Double.parseDouble(main.get("temp_min").toString());
        double tempMax = Double.parseDouble(main.get("temp_max").toString());
        double pressure = Double.parseDouble(main.get("pressure").toString());
        double humidity = Double.parseDouble(main.get("humidity").toString());

        long sunrise = Long.parseLong(sys.get("sunrise").toString());
        long sunset = Long.parseLong(sys.get("sunset").toString());

        List<Map<String, Object>> weatherList = (List<Map<String, Object>>) body.get("weather");
        String weatherDescription = (String) weatherList.get(0).get("description");

        return new CityWeatherResponse(
                city.getCityCode(),
                city.getCityName(),
                temp,
                weatherDescription,
                tempMin,
                tempMax,
                sunrise,
                sunset,
                pressure,
                humidity,
                timezone
        );
    }
}
