package com.ruchij.reactive;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class SingleResultSubscriber<T> implements Subscriber<T> {
    private final CompletableFuture<Optional<T>> completableFuture = new CompletableFuture<>();

    @Override
    public void onSubscribe(Subscription subscription) {
        subscription.request(1);
    }

    @Override
    public void onNext(T value) {
        completableFuture.complete(Optional.ofNullable(value));
    }

    @Override
    public void onError(Throwable throwable) {
        completableFuture.completeExceptionally(throwable);
    }

    @Override
    public void onComplete() {
        completableFuture.complete(Optional.empty());
    }

    public CompletableFuture<Optional<T>> toCompletableFuture() {
        return completableFuture;
    }
}
