package org.mundm.wetter.server;

import org.mundm.wetter.server.WeatherProvider.Weather;
import org.mundm.wetter.util.DateTimeHelper;
import org.mundm.wetter.util.trie.Try;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

public class WeatherServer {
    public boolean isRunning() { return running; }

    public void setRunning(boolean running) { this.running = running; }

    private boolean running;
    private WeatherProvider weatherProvider;

    WeatherServer(WeatherProvider weatherProvider) {
        this.running = true;
        this.weatherProvider = weatherProvider;
    }

    void start() throws IOException {
        ServerSocket serving = new ServerSocket(4040);

        while (running) {
            Socket connectionSocket = serving.accept();
            ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(connectionSocket.getOutputStream());

            // this is a blocking call that will only advance if it was possible to read an object "\n"
            Try<LocalDate> request = Try.applyThrowing(() -> (LocalDate) in.readObject());

            Runnable handleRequest = () -> {
                Try<List<Weather>> result = request.flatMap(weatherProvider::getWeatherFor);
                try {
                    out.writeObject(result);
                } catch (IOException e) {
                    System.out.println(
                            String.format(
                                    "%s: Couldn't send response to client: %s \n Response was: %s",
                                    Thread.currentThread().getName(),
                                    e.getMessage(),
                                    result.toString()
                            ));

                }
            };

            new Thread(handleRequest).start();
        }
    }
}
