package com.ruchij.daos.message;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.ruchij.daos.message.models.Message;
import com.ruchij.reactive.MultipleResultSubscriber;
import com.ruchij.reactive.SingleResultSubscriber;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MongoMessageDao implements MessageDao {
    private final MongoCollection<Message> collection;

    public MongoMessageDao(MongoCollection<Message> collection) {
        this.collection = collection;
    }

    @Override
    public CompletableFuture<Integer> insert(Message message) {
        SingleResultSubscriber<InsertOneResult> singleResultSubscriber = new SingleResultSubscriber<>();
        collection.insertOne(message).subscribe(singleResultSubscriber);

        return singleResultSubscriber.toCompletableFuture()
            .thenApply(maybeResult ->
                maybeResult.map(insertOneResult -> insertOneResult.wasAcknowledged() ? 1 : 0).orElse(0)
            );
    }

    @Override
    public CompletableFuture<List<Message>> findByUserId(String userId, int pageNumber, int pageSize) {
        MultipleResultSubscriber<Message> multipleResultSubscriber = new MultipleResultSubscriber<>(pageSize);

        collection.find(Filters.or(Filters.eq("senderId", userId), Filters.eq("receiverId", userId)))
            .sort(Sorts.descending("sentAt"))
            .skip(pageNumber * pageSize)
            .limit(pageSize)
            .subscribe(multipleResultSubscriber);

        return multipleResultSubscriber.toCompletableFuture();
    }

}
