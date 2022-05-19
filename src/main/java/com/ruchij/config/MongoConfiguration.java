package com.ruchij.config;

import com.typesafe.config.Config;

public record MongoConfiguration(String connectionUrl, String database) {

    public static MongoConfiguration fromConfig(Config config) {
        Config mongoConfig = config.getConfig("mongo-configuration");

        String connectionUrl = mongoConfig.getString("url");
        String database = mongoConfig.getString("database");

        return new MongoConfiguration(connectionUrl, database);
    }

}
