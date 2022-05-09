package com.ruchij.pubsub.kafka;

import com.ruchij.avro.chat.OneToOneMessage;
import com.ruchij.services.messages.models.Message;
import com.ruchij.services.messages.models.OneToOne;
import org.apache.avro.specific.SpecificRecord;
import org.joda.time.DateTime;

import java.time.Instant;

public interface KafkaTopic<A extends Message, B extends SpecificRecord> {
    KafkaTopic<OneToOne, OneToOneMessage> ONE_TO_ONE = new KafkaTopic<>() {
        @Override
        public String topicName() {
            return "one-to-one";
        }

        @Override
        public OneToOne fromSpecificRecord(OneToOneMessage oneToOneMessage) {
            return new OneToOne(
                oneToOneMessage.getMessageId().toString(),
                oneToOneMessage.getSenderId().toString(),
                new DateTime(oneToOneMessage.getSentAt().toEpochMilli()),
                oneToOneMessage.getReceiverId().toString(),
                oneToOneMessage.getMessage().toString()
            );
        }

        @Override
        public OneToOneMessage toSpecificRecord(OneToOne oneToOne) {
            return OneToOneMessage.newBuilder()
                .setMessageId(oneToOne.messageId())
                .setSenderId(oneToOne.senderId())
                .setSentAt(Instant.ofEpochMilli(oneToOne.sentAt().getMillis()))
                .setReceiverId(oneToOne.receiverId())
                .setMessage(oneToOne.message())
                .build();
        }
    };

    String topicName();

    A fromSpecificRecord(B record);

    B toSpecificRecord(A value);
}
