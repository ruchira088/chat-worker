package com.ruchij.daos.message;

import com.ruchij.daos.message.models.Message;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface MessageDao {
    CompletableFuture<Integer> insert(Message message);

    CompletableFuture<List<Message>> findByUserId(String userId, int pageNumber, int pageSize);
}
