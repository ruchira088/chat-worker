package com.ruchij;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.ruchij.avro.chat.OneToOneMessage;
import com.ruchij.config.ApiServiceConfiguration;
import com.ruchij.config.KafkaConfiguration;
import com.ruchij.config.MongoConfiguration;
import com.ruchij.daos.message.MongoMessageDao;
import com.ruchij.daos.message.models.MongoMessage;
import com.ruchij.daos.mongo.Mongo;
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
        MongoConfiguration mongoConfiguration = MongoConfiguration.fromConfig(config);

        run(kafkaConfiguration, apiServiceConfiguration, mongoConfiguration);
    }

    public static void run(KafkaConfiguration kafkaConfiguration, ApiServiceConfiguration apiServiceConfiguration, MongoConfiguration mongoConfiguration) {
        try (MongoClient mongoClient = Mongo.createClient(mongoConfiguration)) {
            MongoDatabase mongoDatabase = mongoClient.getDatabase(mongoConfiguration.database());

            MongoMessageDao mongoMessageDao = new MongoMessageDao(mongoDatabase);

            ApiService apiService = new ApiServiceImpl(HttpClient.newHttpClient(), apiServiceConfiguration);

            KafkaSubscriber<OneToOne, OneToOneMessage> oneToOneKafkaSubscriber =
                new KafkaSubscriber<>(KafkaTopic.ONE_TO_ONE, kafkaConfiguration);

            OneToOneMessageHandler oneToOneMessageHandler = new OneToOneMessageHandler(apiService, mongoMessageDao);

            ChatWorker<OneToOne, OneToOneMessage> oneToOneChatWorker =
                new ChatWorker<>(oneToOneKafkaSubscriber, oneToOneMessageHandler);

            oneToOneChatWorker.run();
        }

    }
}
