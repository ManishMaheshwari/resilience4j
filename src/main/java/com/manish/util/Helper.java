package com.manish.util;

import reactor.core.publisher.Flux;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class Helper {

    public static Consumer dataConsumer = T -> System.out.format("dataConsumer: %s%n", T);
    public static Consumer errorConsumer = T -> System.out.format("errorConsumer: %s%n", T);

    public static void divider() {
        System.out.println("=======================");
    }

    public static void divider(String message) {
        System.out.format("=========== %s  ============%n", message);
    }

    public static Flux<Integer> getErroringFlux() {
        Flux<Integer> ints = Flux.range(1, 40)
                .map(i -> {
                    if (i <= 3) return i;
                    throw new RuntimeException("Got an error here - Reached 4");
                });
        return ints;
    }

    public static void hold(int seconds) throws InterruptedException {
        TimeUnit.SECONDS.sleep(seconds);
    }

    public static void holdMillis(int milliSeconds) throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(milliSeconds);
    }

    public static String blockingClient(String url) {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Body of site - " + url;

    }

}
