package com.ruchij.config;

public record KafkaConfiguration(String bootstrapServers, String schemaRegistryUrl) {
}
