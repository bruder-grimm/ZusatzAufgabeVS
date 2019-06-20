package org.mundm.wetter.server;

public class Main {
    public static void main(String[] args) throws Exception {
        WeatherProvider weatherProvider = new WeatherProvider("berlin");

        WeatherServer weatherServer = new WeatherServer(weatherProvider);
        weatherServer.start();

        Runtime.getRuntime().addShutdownHook(
                new Thread(weatherServer::stop)
        );
    }
}
