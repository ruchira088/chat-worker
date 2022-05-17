package com.ruchij.services.messages.models;

import com.ruchij.daos.message.models.Message;
import org.joda.time.DateTime;

import java.util.Optional;

public record OneToOne(String messageId, String senderId, DateTime sentAt, String receiverId,
                       String content) implements IncomingMessage {
    public Message toMessage() {
        return new Message(messageId, senderId, sentAt, receiverId, Optional.empty(), content, Optional.empty(), Optional.empty());
    }
}
