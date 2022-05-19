package com.ruchij.services.messages;

import com.ruchij.daos.message.MessageDao;
import com.ruchij.services.api.ApiService;
import com.ruchij.services.messages.models.OneToOne;

import java.util.concurrent.CompletableFuture;

public class OneToOneMessageHandler implements MessageHandler<OneToOne> {
    private final ApiService apiService;
    private final MessageDao messageDao;

    public OneToOneMessageHandler(ApiService apiService, MessageDao messageDao) {
        this.apiService = apiService;
        this.messageDao = messageDao;
    }

    @Override
    public CompletableFuture<Boolean> handle(OneToOne oneToOne) {
        CompletableFuture<Integer> messageInsertionResult = messageDao.insert(oneToOne.toMessage());

        return apiService.deliver(oneToOne)
            .thenCompose(delivered -> messageInsertionResult.thenApply(result -> delivered));
    }
}
