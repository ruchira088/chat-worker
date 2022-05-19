package com.ruchij.daos.message.models;

import org.joda.time.DateTime;

import java.util.Optional;

public class MongoMessage {
    private String messageId;
    private String senderId;
    private DateTime sentAt;
    private String receiverId;
    private String groupId;
    private String content;
    private DateTime deliveredAt;
    private DateTime readAt;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public DateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(DateTime sentAt) {
        this.sentAt = sentAt;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(DateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public DateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(DateTime readAt) {
        this.readAt = readAt;
    }

    public Message toMessage() {
        return new Message(
            getMessageId(),
            getSenderId(),
            getSentAt(),
            getReceiverId(),
            Optional.ofNullable(getGroupId()),
            getContent(),
            Optional.ofNullable(getDeliveredAt()),
            Optional.ofNullable(getReadAt())
        );
    }

    public static MongoMessage fromMessage(Message message) {
        MongoMessage mongoMessage = new MongoMessage();

        mongoMessage.setMessageId(message.messageId());
        mongoMessage.setSenderId(message.senderId());
        mongoMessage.setSentAt(message.sentAt());
        mongoMessage.setReceiverId(message.receiverId());
        mongoMessage.setGroupId(message.groupId().orElse(null));
        mongoMessage.setContent(message.content());
        mongoMessage.setDeliveredAt(message.deliveredAt().orElse(null));
        mongoMessage.setReadAt(message.readAt().orElse(null));

        return mongoMessage;
    }

}
