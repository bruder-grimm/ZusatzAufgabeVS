package org.mundm.wetter.client;

import java.net.InetAddress;

public class Main {
    public static void main(String[] args) throws Exception {
        String ip = args.length == 0 ? "localhost" : args[0];

        InetAddress server = InetAddress.getByName(ip);
        WeatherClient weatherClient = new WeatherClient(server);

        weatherClient.start();

        Runtime.getRuntime().addShutdownHook(new Thread(weatherClient::stop));
    }
}
