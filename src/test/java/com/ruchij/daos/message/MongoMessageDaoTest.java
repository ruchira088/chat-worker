package com.ruchij.daos.message;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.InsertManyResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.ruchij.reactive.MultipleResultSubscriber;
import com.ruchij.reactive.SingleResultSubscriber;
import com.ruchij.tests.AbstractMongoDbTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MongoMessageDaoTest extends AbstractMongoDbTest {

    @Test
    void insertingAndReadingData() throws Exception {
        MongoCollection<Person> collection = mongoDatabase().getCollection("person", Person.class);

        SingleResultSubscriber<InsertManyResult> resultSubscriber = new SingleResultSubscriber<>();

        collection.insertMany(List.of(new Person("John", 1), new Person("Mary", 1)))
            .subscribe(resultSubscriber);

        CompletableFuture<List<Person>> people = resultSubscriber.toCompletableFuture()
            .thenCompose(insertManyResult -> {
                MultipleResultSubscriber<Person> multipleResultSubscriber = new MultipleResultSubscriber<>(10);

                collection.find(Filters.eq("age", 1)).subscribe(multipleResultSubscriber);

                return multipleResultSubscriber.toCompletableFuture();
            });

        List<Person> personList = people.get(10, TimeUnit.SECONDS);

        assertEquals(2, personList.size());
        assertTrue(personList.contains(new Person("John", 1)));
        assertTrue(personList.contains(new Person("Mary", 1)));
    }

}