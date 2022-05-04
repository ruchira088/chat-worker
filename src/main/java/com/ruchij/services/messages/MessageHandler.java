package com.ruchij.services.messages;

import java.util.concurrent.CompletableFuture;

public interface MessageHandler<T> {
    CompletableFuture<Boolean> handle(T message);
}
