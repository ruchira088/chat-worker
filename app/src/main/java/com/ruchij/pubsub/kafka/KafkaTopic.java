package com.ruchij.pubsub.kafka;

import org.apache.avro.specific.SpecificRecord;

public interface KafkaTopic<A, B extends SpecificRecord> {
    String topicName();

    A fromSpecificRecord(B record);

    B toSpecificRecord(A value);
}
