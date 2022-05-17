package com.ruchij.daos.message.models;

import org.joda.time.DateTime;

import java.util.Optional;

public record Message(String messageId, String senderId, DateTime sentAt, String receiverId, Optional<String> groupId,
                      String content, Optional<DateTime> deliveredAt, Optional<DateTime> readAt) {
}
