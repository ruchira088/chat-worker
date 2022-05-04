package com.ruchij.pubsub.kafka;

import com.ruchij.config.KafkaConfiguration;
import com.ruchij.pubsub.Subscriber;
import com.ruchij.pubsub.models.CommittableRecord;
import com.ruchij.services.messages.models.Message;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class KafkaSubscriber<A extends Message, B extends SpecificRecord> implements Subscriber<A> {
    private final KafkaConfiguration kafkaConfiguration;

    private final KafkaTopic<A, B> kafkaTopic;

    public KafkaSubscriber(KafkaTopic<A, B> kafkaTopic, KafkaConfiguration kafkaConfiguration) {
        this.kafkaTopic = kafkaTopic;
        this.kafkaConfiguration = kafkaConfiguration;
    }

    @Override
    public Stream<CommittableRecord<A>> subscribe(String groupId) {
        try (KafkaConsumer<String, B> kafkaConsumer = new KafkaConsumer<>(consumerProperties(groupId))) {
            kafkaConsumer.subscribe(List.of(kafkaTopic.topicName()));

            return Stream.generate(() -> kafkaConsumer.poll(Duration.ofMillis(100)))
                    .flatMap(consumerRecords -> StreamSupport.stream(consumerRecords.spliterator(), false))
                    .map(consumerRecord ->
                            new CommittableRecord<>(
                                    kafkaTopic.fromSpecificRecord(consumerRecord.value()),
                                    () -> {
                                        CompletableFuture<Void> completableFuture = new CompletableFuture<>();

                                        kafkaConsumer.commitAsync(
                                                Map.of(
                                                        new TopicPartition(consumerRecord.topic(), consumerRecord.partition()),
                                                        new OffsetAndMetadata(consumerRecord.offset())
                                                ),
                                                (offsets, exception) -> {
                                                    if (exception == null) {
                                                        completableFuture.complete(null);
                                                    } else {
                                                        completableFuture.completeExceptionally(exception);
                                                    }
                                                }
                                        );

                                        return completableFuture;
                                    }
                            )
                    );
        }
    }


    private Properties consumerProperties(String groupId) {
        Properties properties = new Properties();

        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfiguration.bootstrapServers());
        properties.put(AbstractKafkaSchemaSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, kafkaConfiguration.schemaRegistryUrl());

        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());

        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        return properties;
    }
}
