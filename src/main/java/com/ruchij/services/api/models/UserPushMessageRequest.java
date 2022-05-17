package com.ruchij.services.api.models;

import com.ruchij.services.messages.models.IncomingMessage;

public record UserPushMessageRequest(String receiverId, IncomingMessage message) {
}
