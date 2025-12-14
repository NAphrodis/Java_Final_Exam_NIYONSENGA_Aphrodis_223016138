package com.agriportal.util;

import java.time.LocalDate;


public class WeatherService {

    public static class Forecast {
        public LocalDate date;
        public String condition;
        public double avgTempC;
        public double expectedRainMm;

        public Forecast(LocalDate date, String condition, double avgTempC, double expectedRainMm) {
            this.date = date;
            this.condition = condition;
            this.avgTempC = avgTempC;
            this.expectedRainMm = expectedRainMm;
        }
    }

    
    public Forecast[] getFiveDayForecast(String location) {
        Forecast[] arr = new Forecast[5];
        LocalDate d = LocalDate.now();
        for (int i = 0; i < 5; i++) {
            String cond = (i % 3 == 0) ? "Sunny" : (i % 3 == 1) ? "Cloudy" : "Rainy";
            arr[i] = new Forecast(d.plusDays(i), cond, 20 + i, (i % 3 == 2) ? 5.0 + i : 0.0);
        }
        return arr;
    }
}
