package com.ruchij.services.messages;

import com.ruchij.services.api.ApiService;
import com.ruchij.services.messages.models.OneToOne;

public class MessageHandlerImpl implements MessageHandler {
    private final ApiService apiService;

    public MessageHandlerImpl(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void handle(OneToOne oneToOne) {

    }
}
