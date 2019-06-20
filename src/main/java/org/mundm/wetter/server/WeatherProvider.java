package org.mundm.wetter.server;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class WeatherProvider {
    private String city;
    private String countryCode;

    private String assembleUrl(String city, String countryCode, LocalDate day) {
        long startOfDay = day.atStartOfDay().toInstant(ZoneOffset.UTC).getEpochSecond();
        long endOfDay = startOfDay + 60 * 60 * 24; // end of day
        return String.format(
                "http://history.openweathermap.org/data/2.5/history/city?q=%s,%s&type=hour&start=%s&cnt=%s",
                city,
                countryCode,
                startOfDay,
                endOfDay
        );
    }

    class Weather {
        private LocalDateTime hour;
        private double temperature;

        public Weather(LocalDateTime hour, double temperature) {
            this.hour = hour;
            this.temperature = temperature;
        }
    }

    public List<Weather> getWeatherFor(LocalDate date) {
        String url = assembleUrl(this.city, this.countryCode, date);
        new URL(url).openConnection();
    }
}
