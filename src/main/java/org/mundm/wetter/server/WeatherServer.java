package org.mundm.wetter.server;

import org.mundm.wetter.Weather;
import org.mundm.wetter.util.trie.Try;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;

public class WeatherServer {
    private boolean running;
    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }

    private WeatherProvider weatherProvider;

    private ServerSocket serving;
    private Socket connectionSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    WeatherServer(WeatherProvider weatherProvider) {
        this.running = true;
        this.weatherProvider = weatherProvider;
    }

    public void start() throws IOException {
        System.out.println("Creating Socket and waiting for connection...");
        serving = new ServerSocket(4040);
        connectionSocket = serving.accept();
        System.out.println("Connection established");

        in = new ObjectInputStream(connectionSocket.getInputStream());
        out = new ObjectOutputStream(connectionSocket.getOutputStream());

        while (isRunning()) {
            System.out.println("Waiting for request");
            Try<LocalDate> request = Try.applyThrowing(() -> (LocalDate) in.readObject());
            System.out.println("Received object from socket");

            handleRequest(request);
        }

        stop();
    }

    public void stop() {
        try {
            serving.close();
            connectionSocket.close();
            in.close();
            out.close();
        } catch (IOException ignored) {}
    }

    private void handleRequest(Try<LocalDate> request) {
        Runnable requestHandler = getRequestHandlerFor(request);
        new Thread(requestHandler).start();
    }

    private Runnable getRequestHandlerFor(Try<LocalDate> possibleDate) {
        return () -> {
            System.out.println(String.format("Started Thread %s", Thread.currentThread().getName()));
            Try<List<Weather>> result = possibleDate.flatMap(weatherProvider::getWeatherFor);

            sendToClient(result);
        };
    }

    private synchronized void sendToClient(Try<List<Weather>> result) {
        try {
            out.writeObject(result);
            System.out.println(String.format("%s: Sent response to socket", Thread.currentThread().getName()));
        } catch (IOException e) {
            System.out.println(
                    String.format(
                            "%s: Couldn't send response to client: %s \n Response was: %s",
                            Thread.currentThread().getName(),
                            e.getMessage(),
                            result.toString()
                    ));
        }
    }
}
