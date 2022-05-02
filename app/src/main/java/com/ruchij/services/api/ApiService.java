package com.ruchij.services.api;

import com.ruchij.services.messages.models.OneToOne;

import java.util.concurrent.CompletableFuture;

public interface ApiService {
    CompletableFuture<Boolean> deliver(OneToOne oneToOne);
}
