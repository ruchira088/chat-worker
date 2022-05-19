package com.ruchij.daos.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.ruchij.config.MongoConfiguration;
import com.ruchij.daos.mongo.codecs.DateTimeCodec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

public class Mongo {

    public static MongoClient createClient(MongoConfiguration mongoConfiguration) {
        ConnectionString connectionString = new ConnectionString(mongoConfiguration.connectionUrl());

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
            CodecRegistries.fromCodecs(new DateTimeCodec()),
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .codecRegistry(codecRegistry)
            .build();

        return MongoClients.create(mongoClientSettings);
    }

}
