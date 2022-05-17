package com.ruchij.reactive;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MultipleResultSubscriber<T> implements Subscriber<T> {
    private final int size;
    private final CompletableFuture<List<T>> completableFuture = new CompletableFuture<>();
    private final List<T> result = new ArrayList<>();

    public MultipleResultSubscriber(int size) {
        this.size = size;
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(size);
    }

    @Override
    public void onNext(T value) {
        result.add(value);
    }

    @Override
    public void onError(Throwable throwable) {
        completableFuture.completeExceptionally(throwable);
    }

    @Override
    public void onComplete() {
        completableFuture.complete(result);
    }

    public CompletableFuture<List<T>> toCompletableFuture() {
        return completableFuture;
    }
}
