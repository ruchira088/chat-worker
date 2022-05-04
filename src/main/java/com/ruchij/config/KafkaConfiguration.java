package com.ruchij.config;

import com.typesafe.config.Config;

public record KafkaConfiguration(String bootstrapServers, String schemaRegistryUrl) {

    public static KafkaConfiguration fromConfig(Config config) {
        Config kafkaConfig = config.getConfig("kafka-configuration");

        String bootstrapServers = kafkaConfig.getString("bootstrap-servers");
        String schemaRegistryUrl = kafkaConfig.getString("schema-registry-url");

        return new KafkaConfiguration(bootstrapServers, schemaRegistryUrl);
    }

}
