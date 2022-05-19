package com.ruchij.tests;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.ruchij.config.MongoConfiguration;
import com.ruchij.daos.mongo.Mongo;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
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

        MongoConfiguration mongoConfiguration =
            new MongoConfiguration("mongodb://localhost:%s".formatted(mongoPort), "chat-worker");

        mongoClient = Mongo.createClient(mongoConfiguration);
        mongoDatabase = mongoClient.getDatabase(mongoConfiguration.database());
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
