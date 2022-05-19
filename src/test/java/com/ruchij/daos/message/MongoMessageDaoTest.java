package com.ruchij.daos.message;

import com.mongodb.reactivestreams.client.MongoCollection;
import com.ruchij.daos.message.models.Message;
import com.ruchij.daos.message.models.MongoMessage;
import com.ruchij.tests.AbstractMongoDbTest;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoMessageDaoTest extends AbstractMongoDbTest {

    @Test
    void insertingAndReadingData() throws Exception {
        DateTime dateTime = DateTime.now();

        Message oneToOneMessage =
            new Message(
                "my-message-id",
                "sender-id",
                dateTime,
                "my-id",
                Optional.empty(),
                "This is a test message",
                Optional.of(dateTime),
                Optional.of(dateTime)
            );

        Message groupMessage =
            new Message(
                "my-group-message-id",
                "group-sender-id",
                dateTime,
                "my-id",
                Optional.of("my-group-id"),
                "This is a group test message",
                Optional.of(dateTime),
                Optional.empty()
            );

        MongoMessageDao mongoMessageDao = new MongoMessageDao(mongoDatabase());

        List<Message> messages = mongoMessageDao.insert(oneToOneMessage)
            .thenCompose(result -> mongoMessageDao.insert(groupMessage))
            .thenCompose(result -> mongoMessageDao.findByUserId("my-id", 0, 10))
            .get(5, TimeUnit.SECONDS);

        assertEquals(messages, List.of(oneToOneMessage, groupMessage));

        List<Message> senderResults = mongoMessageDao.findByUserId("sender-id", 0, 10).join();

        assertEquals(senderResults, List.of(oneToOneMessage));

        List<Message> emptyResults = mongoMessageDao.findByUserId("no-id", 0, 10).join();

        assertEquals(emptyResults, List.of());
    }

}