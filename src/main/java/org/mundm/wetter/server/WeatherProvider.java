package org.mundm.wetter.server;

import org.mundm.wetter.util.DateTimeHelper;
import org.mundm.wetter.util.trie.Try;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeatherProvider {
    private static final int TIMESTAMP_POS = 2;
    private static final int TEMP_POS = 3;
    private String city;
    private DateTimeHelper dateTimeHelper;

    public class Weather {
        public LocalDateTime getHour() { return hour; }
        public double getTemperature() { return temperature; }

        private LocalDateTime hour;
        private double temperature;

        Weather(LocalDateTime hour, double temperature) {
            this.hour = hour;
            this.temperature = temperature;
        }
    }

    Try<List<Weather>> getWeatherFor(LocalDate date) {
        Try<Stream<String>> stream = Try.applyThrowing(() ->
                Files.lines(Paths.get(String.format("%s.csv", city)))
        );

        return stream.map(lines ->
                lines.map(line -> {
                    String[] weatherData = line.split(",");

                    double temperature = Double.parseDouble(weatherData[TEMP_POS]);
                    LocalDateTime dayAndHour = dateTimeHelper.fromEpochSecond(Long.parseLong(weatherData[TIMESTAMP_POS]));

                    return new Weather(dayAndHour, temperature);
                }).collect(Collectors.toList())
        );
    }
}
