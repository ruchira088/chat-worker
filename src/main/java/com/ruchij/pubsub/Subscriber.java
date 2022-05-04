package com.ruchij.pubsub;

import com.ruchij.pubsub.models.CommittableRecord;

import java.util.stream.Stream;

public interface Subscriber<A> {
    Stream<CommittableRecord<A>> subscribe(String groupId);
}