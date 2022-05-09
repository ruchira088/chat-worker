package com.ruchij.services.messages.models;

import org.joda.time.DateTime;

public record OneToOne(String messageId, String senderId, DateTime sentAt, String receiverId,
                       String message) implements Message {
}
