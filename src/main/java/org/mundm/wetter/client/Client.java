package org.mundm.wetter.client;
import org.mundm.wetter.Weather;
import org.mundm.wetter.util.option.Option;
import org.mundm.wetter.util.trie.Try;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

class Client {

    private ObjectInputStream in;
    private ObjectInputStream out;

    private Socket clientSocket;

    private final Scanner scanner = new Scanner(System.in);

    public Client() { }

    public void run() throws Exception {

        in = new ObjectInputStream();
        out = new ObjectOutputStream();
        forceGetNextString("Gib ein Datum ein", LocalDate.from)

        printToTerminal((Try<List<Weather>>) in.readObject());
    }

    public String printToTerminal(Try<List<Weather>> possibleWeather) {
        Try<String> result = possibleWeather.map(weatherData -> {
            String realString = "";
            for (Weather weather : weatherData) {
                String weatherString = weather.getHour().toString() + " " + weather.getTemperature();
                realString = realString + "\n" + weatherString;
            }
            return realString;
        });

        return result.isSuccess() ? result.get() : result.failure().getMessage();
    }

    private Option<String> getNextString() {
        return Try.apply(scanner::nextLine).flatMap(s ->
                s.isEmpty() ? Try.failed(new Exception()) : Try.successful(s)
        ).toOption(); // there's gonna be exceptions but I so do not care
    }

    private String forceGetNextString(String prompt, Predicate<String> condition) {
        return getString(prompt)
                .filter(condition)
                .orElseGet(() -> {
                    System.out.println("Input was invalid, try again");
                    return forceGetNextString(prompt, condition);
                });
    }

    private Option<String> getString(String prompt) {
        System.out.println(prompt);
        return getNextString();
    }
}