package com.ruchij;

import com.ruchij.avro.chat.OneToOneMessage;
import com.ruchij.config.ApiServiceConfiguration;
import com.ruchij.config.KafkaConfiguration;
import com.ruchij.pubsub.kafka.KafkaSubscriber;
import com.ruchij.pubsub.kafka.KafkaTopic;
import com.ruchij.services.api.ApiService;
import com.ruchij.services.api.ApiServiceImpl;
import com.ruchij.services.messages.OneToOneMessageHandler;
import com.ruchij.services.messages.models.OneToOne;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.net.http.HttpClient;

public class App {
    public static void main(String[] args) {
        Config config = ConfigFactory.load();
        KafkaConfiguration kafkaConfiguration = KafkaConfiguration.fromConfig(config);
        ApiServiceConfiguration apiServiceConfiguration = ApiServiceConfiguration.fromConfig(config);

        run(kafkaConfiguration, apiServiceConfiguration);
    }

    public static void run(KafkaConfiguration kafkaConfiguration, ApiServiceConfiguration apiServiceConfiguration) {
        ApiService apiService = new ApiServiceImpl(HttpClient.newHttpClient(), apiServiceConfiguration);

        KafkaSubscriber<OneToOne, OneToOneMessage> oneToOneKafkaSubscriber =
            new KafkaSubscriber<>(KafkaTopic.ONE_TO_ONE, kafkaConfiguration);

        OneToOneMessageHandler oneToOneMessageHandler = new OneToOneMessageHandler(apiService);

        ChatWorker<OneToOne, OneToOneMessage> oneToOneChatWorker =
            new ChatWorker<>(oneToOneKafkaSubscriber, oneToOneMessageHandler);

        oneToOneChatWorker.run();
    }

    public String getGreeting() {
        return "Hello World!";
    }
}
