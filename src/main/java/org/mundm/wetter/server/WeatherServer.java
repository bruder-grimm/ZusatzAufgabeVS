package org.mundm.wetter.server;

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
    private DateTimeHelper dateTimeHelper;

    public WeatherServer(WeatherProvider weatherProvider, DateTimeHelper dateTimeHelper) {
        this.running = true;
        this.weatherProvider = weatherProvider;
        this.dateTimeHelper = dateTimeHelper;
    }

    public void run() throws IOException {
        ServerSocket serving = new ServerSocket(4040);

        while (running) {
            Socket connectionSocket = serving.accept();
            ObjectInputStream in = new ObjectInputStream(connectionSocket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(connectionSocket.getOutputStream());

            // this is a blocking call that will only advance if it was possible to read an object "\n"
            Try<LocalDate> request = Try.applyThrowing(() -> (LocalDate) in.readObject());

            Runnable handleRequest = () -> {
                request.flatMap(date -> Try.applyThrowing(() -> {
                    Try<List<WeatherProvider.Weather>> result = weatherProvider.getWeatherFor(date);
                    out.writeObject(request);
                    return result;
                }));
            };
        }
    }
}
