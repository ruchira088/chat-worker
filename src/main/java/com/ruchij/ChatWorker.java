package com.ruchij;

import com.ruchij.pubsub.kafka.KafkaSubscriber;
import com.ruchij.services.messages.MessageHandler;
import com.ruchij.services.messages.models.Message;
import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class ChatWorker<A extends Message, B extends SpecificRecord> {
    private final Logger logger = LoggerFactory.getLogger(ChatWorker.class);

    private final KafkaSubscriber<A, B> kafkaSubscriber;
    private final MessageHandler<A> messageHandler;

    private static final String CONSUMER_GROUP = "chat-worker";

    public ChatWorker(KafkaSubscriber<A, B> kafkaSubscriber, MessageHandler<A> messageHandler) {
        this.kafkaSubscriber = kafkaSubscriber;
        this.messageHandler = messageHandler;
    }

    public void run() {
        this.kafkaSubscriber.subscribe(CONSUMER_GROUP)
            .forEach(committableRecord -> {
                CompletableFuture<Boolean> result = messageHandler.handle(committableRecord.data())
                        .thenCompose(value -> committableRecord.commitData().get().thenApply(voidValue -> value));

                try {
                    Boolean pushed = result.join();
                    logger.info("messageId=%s push=%s".formatted(committableRecord.data().messageId(), pushed));
                } catch (Exception exception) {
                    logger.error("Failed to handle messageId=%s".formatted(committableRecord.data().messageId()), exception);
                }
            });
    }
}
