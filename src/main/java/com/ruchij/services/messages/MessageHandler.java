package com.ruchij.services.messages;

import com.ruchij.services.messages.models.IncomingMessage;

import java.util.concurrent.CompletableFuture;

public interface MessageHandler<T extends IncomingMessage> {
    CompletableFuture<Boolean> handle(T message);
}
