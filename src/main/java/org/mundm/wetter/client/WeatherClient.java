package org.mundm.wetter.client;

import org.mundm.wetter.Weather;
import org.mundm.wetter.util.option.Option;
import org.mundm.wetter.util.trie.Try;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

class WeatherClient {
    private final Scanner scanner = new Scanner(System.in);

    private boolean running;

    private InetAddress ip;

    private Socket clientSocket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public WeatherClient(InetAddress ip) {
        this.ip = ip;
        this.running = true;
    }

    public void start() throws Exception {
        System.out.println("Creating Socket and waiting for connection...");
        clientSocket = new Socket(ip, 4040);
        System.out.println("Connection established");

        in = new ObjectInputStream(clientSocket.getInputStream());
        out = new ObjectOutputStream(clientSocket.getOutputStream());

        while (running) {
            Option<String> possibleInput = getString("Please enter a date like: YYYY-mm-dd");
            Option<LocalDate> possibleDate = parseFrom(possibleInput);

            if (!possibleDate.isPresent()) {
                System.out.println(String.format("Input was invalid: Couldn't parse %s", possibleInput));
                continue;
            }

            LocalDate date = possibleDate.get();
            out.writeObject(date);

            @SuppressWarnings("unchecked")
            Try<List<Weather>> response = (Try<List<Weather>>) in.readObject();

            System.out.println(toResult(response));
        }

        stop();
    }

    public void stop() {
        try {
            clientSocket.close();
            in.close();
            out.close();
        } catch (IOException ignored) { }
    }

    private Option<LocalDate> parseFrom(Option<String> possibleInput) {
        return possibleInput
                .flatMap(userInput -> {
                    Try<LocalDate> parsed = Try.apply(() -> LocalDate.parse(userInput));
                    return parsed.toOption();
                });
    }

    private String toResult(Try<List<Weather>> possibleWeather) {
        Try<String> result = possibleWeather.map(weatherData -> {
            StringBuilder realString = new StringBuilder();

            if (weatherData.isEmpty()) return "No weatherdata received for entered date";

            for (Weather weather : weatherData) {
                String weatherString = String.format(
                        "Wetterstation: %3d, Temperatur: %4.1f, Uhrzeit: %02d",
                        weather.getWeatherStationId(),
                        weather.getTemperature(),
                        weather.getHour().getHour()
                );
                realString.append("\n").append(weatherString);
            }

            Supplier<DoubleStream> temperatures = () -> weatherData
                    .stream()
                    .map(Weather::getTemperature)
                    .mapToDouble(Double::valueOf);

            double max = temperatures.get().max().orElse(0);
            double min = temperatures.get().min().orElse(0);
            double avg = temperatures.get().average().orElse(0);

            realString
                    .append("\n")
                    .append(String.format("Minimum: %4.1f", min))
                    .append(String.format("Maximum: %4.1f", max))
                    .append(String.format("Average: %4.1f", avg));

            return realString.toString();
        });

        return result.isSuccess() ? result.get() : result.failure().toString();
    }

    private Option<String> getString(String prompt) {
        System.out.println(prompt);
        return getNextString();
    }

    private Option<String> getNextString() {
        return Try.apply(scanner::nextLine).flatMap(s ->
                s.isEmpty() ? Try.failed(new Exception()) : Try.successful(s)
        ).toOption(); // there's gonna be exceptions but I so do not care
    }

    public boolean isRunning() { return running; }
    public void setRunning(boolean running) { this.running = running; }
}