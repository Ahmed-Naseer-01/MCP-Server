package com.example.Weather_server.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class WeatherService {

    private final RestClient restClient;

    public WeatherService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.weather.gov")
                .defaultHeader("Accept", "application/geo+json")
                .defaultHeader("User-Agent", "WeatherApiClient/1.0 (your@email.com)")
                .build();
    }

    @Tool(description = "Get weather forecast for a specific latitude/longitude")
    public String getWeatherForecastByLocation(
            double latitude,   // Latitude coordinate
            double longitude   // Longitude coordinate
    ) {

        String pointsUrl = String.format("/points/%f,%f", latitude, longitude);
        var pointsResponse = restClient.get().uri(pointsUrl).retrieve().body(Map.class);
        if (pointsResponse == null || !pointsResponse.containsKey("properties")) {
            return "Unable to retrieve forecast URL for the given location.";
        }
        var properties = (Map<?, ?>) pointsResponse.get("properties");
        Object forecastUrlObj = properties.get("forecast");
        if (forecastUrlObj == null) {
            return "Forecast URL not found for the given location.";
        }
        String forecastUrl = forecastUrlObj.toString().replace("https://api.weather.gov", "");

        var forecastResponse = restClient.get().uri(forecastUrl).retrieve().body(Map.class);
        if (forecastResponse == null || !forecastResponse.containsKey("properties")) {
            return "Unable to retrieve forecast data.";
        }
        var forecastProperties = (Map<?, ?>) forecastResponse.get("properties");
        var periods = (Iterable<?>) forecastProperties.get("periods");
        if (periods == null) {
            return "No forecast periods found.";
        }
        StringBuilder sb = new StringBuilder();
        for (Object periodObj : periods) {
            Map<?, ?> period = (Map<?, ?>) periodObj;
            sb.append(period.get("name")).append(": ")
              .append(period.get("detailedForecast")).append("\n")
              .append("Temperature: ").append(period.get("temperature")).append(" ").append(period.get("temperatureUnit")).append("\n")
              .append("Wind: ").append(period.get("windSpeed")).append(" ").append(period.get("windDirection")).append("\n\n");
        }
        return sb.toString();
    }

    @Tool(description = "Get weather alerts for a US state")
    public String getAlerts(
            @ToolParam(description = "Two-letter US state code (e.g. CA, NY)") String state
    ) {
        String alertsUrl = String.format("/alerts/active?area=%s", state);
        var alertsResponse = restClient.get().uri(alertsUrl).retrieve().body(Map.class);
        if (alertsResponse == null || !alertsResponse.containsKey("features")) {
            return "Unable to retrieve alerts for the given state.";
        }
        var features = (Iterable<?>) alertsResponse.get("features");
        StringBuilder sb = new StringBuilder();
        int count = 0;
        for (Object featureObj : features) {
            Map<?, ?> feature = (Map<?, ?>) featureObj;
            var properties = (Map<?, ?>) feature.get("properties");
            sb.append("Event: ").append(properties.get("event")).append("\n")
              .append("Area: ").append(properties.get("areaDesc")).append("\n")
              .append("Severity: ").append(properties.get("severity")).append("\n")
              .append("Description: ").append(properties.get("description")).append("\n")
              .append("Instructions: ").append(properties.get("instruction")).append("\n\n");
            count++;
        }
        if (count == 0) {
            return "No active alerts for this state.";
        }
        return sb.toString();
    }
}
