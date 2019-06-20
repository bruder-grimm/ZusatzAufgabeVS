package org.mundm.wetter;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Weather implements Serializable {
    public LocalDateTime getHour() { return hour; }
    public double getTemperature() { return temperature; }
    public int getWeatherStationId() { return weatherStationId; }

    private LocalDateTime hour;
    private double temperature;


    private int weatherStationId;

    public Weather(LocalDateTime hour, double temperature, int weatherStationId) {
        this.hour = hour;
        this.temperature = temperature;
        this.weatherStationId = weatherStationId;
    }
}
