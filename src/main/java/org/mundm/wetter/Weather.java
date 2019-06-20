package org.mundm.wetter;

import java.time.LocalDateTime;

public class Weather {
    public LocalDateTime getHour() { return hour; }
    public double getTemperature() { return temperature; }

    private LocalDateTime hour;
    private double temperature;

    public Weather(LocalDateTime hour, double temperature) {
        this.hour = hour;
        this.temperature = temperature;
    }
}
