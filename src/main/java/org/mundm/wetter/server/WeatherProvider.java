package org.mundm.wetter.server;

import org.mundm.wetter.Weather;
import org.mundm.wetter.util.trie.Try;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WeatherProvider {
    private static final int WEATHERSTATION_ID = 1;
    private static final int TIMESTAMP_POS = 2;
    private static final int TEMP_POS = 3;

    private final DateTimeFormatter formatter;

    private String city;

    public WeatherProvider(String city) {
        this.city = city;
        formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
    }

    Try<List<Weather>> getWeatherFor(LocalDate date) {
        Try<Stream<String>> stream = Try.applyThrowing(() ->
                Files
                        .lines(Paths.get(ClassLoader.getSystemResource(String.format("%s.csv", city)).toURI()))
                        .skip(1) // skip the headers, sorry for the comment Tobi lol
        );

        return stream.map(lines ->
                lines
                        .map(line -> line
                                .replace("\"", "")
                                .split(",")
                        )
                        .map(weatherData -> {
                            int weatherStationId = Integer.parseInt(weatherData[WEATHERSTATION_ID]);
                            double temperature = Double.parseDouble(weatherData[TEMP_POS]);
                            LocalDateTime dayAndHour = LocalDateTime.parse(weatherData[TIMESTAMP_POS], formatter);

                            return new Weather(dayAndHour, temperature, weatherStationId);
                        })
                        .filter(weather ->
                                weather.getHour().toLocalDate().equals(date)
                        )
                        .collect(Collectors.toList())
        );
    }
}
