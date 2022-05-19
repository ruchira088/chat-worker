package com.ruchij.daos.message;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import com.ruchij.daos.message.models.Message;
import com.ruchij.daos.message.models.MongoMessage;
import com.ruchij.reactive.MultipleResultSubscriber;
import com.ruchij.reactive.SingleResultSubscriber;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class MongoMessageDao implements MessageDao {
    private static final String COLLECTION_NAME = "messages";
    private final MongoCollection<MongoMessage> collection;

    public MongoMessageDao(MongoDatabase mongoDatabase) {
        this.collection = mongoDatabase.getCollection(COLLECTION_NAME, MongoMessage.class);
    }

    @Override
    public CompletableFuture<Integer> insert(Message message) {
        SingleResultSubscriber<InsertOneResult> singleResultSubscriber = new SingleResultSubscriber<>();
        collection.insertOne(MongoMessage.fromMessage(message)).subscribe(singleResultSubscriber);

        return singleResultSubscriber.toCompletableFuture()
            .thenApply(maybeResult ->
                maybeResult.map(insertOneResult -> insertOneResult.wasAcknowledged() ? 1 : 0).orElse(0)
            );
    }

    @Override
    public CompletableFuture<List<Message>> findByUserId(String userId, int pageNumber, int pageSize) {
        MultipleResultSubscriber<MongoMessage> multipleResultSubscriber = new MultipleResultSubscriber<>(pageSize);

        collection.find(Filters.or(Filters.eq("senderId", userId), Filters.eq("receiverId", userId)))
            .sort(Sorts.descending("sentAt", "senderId", "receiverId", "messageId"))
            .skip(pageNumber * pageSize)
            .limit(pageSize)
            .subscribe(multipleResultSubscriber);

        return multipleResultSubscriber.toCompletableFuture()
            .thenApply(mongoMessages -> mongoMessages.stream().map(MongoMessage::toMessage).collect(Collectors.toList()));
    }

}
