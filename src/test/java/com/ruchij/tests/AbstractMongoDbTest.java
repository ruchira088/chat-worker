package com.ruchij.tests;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public abstract class AbstractMongoDbTest {
    private static final MongodStarter mongodStarter = MongodStarter.getDefaultInstance();

    private MongodExecutable mongodExecutable;
    private MongodProcess mongodProcess;

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    @BeforeAll
    void beforeAll() throws Exception {
        int mongoPort = Network.freeServerPort(Network.getLocalHost());

        ImmutableMongodConfig mongodConfig = MongodConfig.builder()
            .version(Version.Main.PRODUCTION)
            .net(new Net(mongoPort, Network.localhostIsIPv6()))
            .build();

        mongodExecutable = mongodStarter.prepare(mongodConfig);
        mongodProcess = mongodExecutable.start();

        ConnectionString connectionString =
            new ConnectionString("mongodb://localhost:%s".formatted(mongoPort));

        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(),
            CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );

        MongoClientSettings clientSettings =
            MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(codecRegistry)
                .build();

        mongoClient = MongoClients.create(clientSettings);
        mongoDatabase = mongoClient.getDatabase("chat-worker");
    }

    protected MongoDatabase mongoDatabase() {
        return mongoDatabase;
    }

    @AfterAll
    void afterAll() throws Exception {
        mongoClient.close();
        mongodProcess.stop();
        mongodExecutable.stop();
    }

}
