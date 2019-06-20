package org.mundm.wetter.server;

import org.mundm.wetter.util.DateTimeHelper;

import java.time.ZoneId;

public class Main {
    public static void main(String[] args) throws Exception {
        DateTimeHelper dateTimeHelper = new DateTimeHelper(ZoneId.systemDefault());
        WeatherProvider weatherProvider = new WeatherProvider("berlin", dateTimeHelper);

        WeatherServer weatherServer = new WeatherServer(weatherProvider);
        weatherServer.start();

        Runtime.getRuntime().addShutdownHook(
                new Thread(weatherServer::stop)
        );
    }
}
