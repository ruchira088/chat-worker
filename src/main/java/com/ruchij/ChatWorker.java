package com.ruchij;

import com.ruchij.pubsub.kafka.KafkaSubscriber;
import com.ruchij.services.messages.MessageHandler;
import com.ruchij.services.messages.models.IncomingMessage;
import org.apache.avro.specific.SpecificRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatWorker<A extends IncomingMessage, B extends SpecificRecord> {
    private static final String CONSUMER_GROUP = "chat-worker";
    private final Logger logger = LoggerFactory.getLogger(ChatWorker.class);
    private final KafkaSubscriber<A, B> kafkaSubscriber;
    private final MessageHandler<A> messageHandler;

    public ChatWorker(KafkaSubscriber<A, B> kafkaSubscriber, MessageHandler<A> messageHandler) {
        this.kafkaSubscriber = kafkaSubscriber;
        this.messageHandler = messageHandler;
    }

    public void run() {
        this.kafkaSubscriber.subscribe(CONSUMER_GROUP)
            .forEach(committableRecord ->
                messageHandler.handle(committableRecord.data())
                    .thenAccept(pushed -> {
                        logger.info("Handled messageId=%s push=%s".formatted(committableRecord.data().messageId(), pushed));
                        committableRecord.commitData().run();
                        logger.info("Committed messageId=%s".formatted(committableRecord.data().messageId()));
                    })
                    .exceptionally(exception -> {
                        logger.error("Failed to handle messageId=%s".formatted(committableRecord.data().messageId()), exception);
                        return null;
                    })
                    .join()
            );
    }
}
