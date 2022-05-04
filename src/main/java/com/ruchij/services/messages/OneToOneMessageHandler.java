package com.ruchij.services.messages;

import com.ruchij.services.api.ApiService;
import com.ruchij.services.messages.models.OneToOne;

import java.util.concurrent.CompletableFuture;

public class OneToOneMessageHandler implements MessageHandler<OneToOne> {
    private final ApiService apiService;

    public OneToOneMessageHandler(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public CompletableFuture<Boolean> handle(OneToOne oneToOne) {
        return apiService.deliver(oneToOne);
    }
}
