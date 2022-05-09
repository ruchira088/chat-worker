package com.ruchij.pubsub.models;

public record CommittableRecord<A>(A data, Runnable commitData) {
}
