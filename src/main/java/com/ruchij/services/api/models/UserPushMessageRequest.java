package com.ruchij.services.api.models;

import com.ruchij.services.messages.models.Message;

public record UserPushMessageRequest(String receiverId, Message message) {
}
