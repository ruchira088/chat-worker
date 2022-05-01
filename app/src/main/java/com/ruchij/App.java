package com.ruchij;

import com.ruchij.config.KafkaConfiguration;
import com.typesafe.config.ConfigFactory;

public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        System.out.println(KafkaConfiguration.fromConfig(ConfigFactory.defaultApplication()));
    }
}
