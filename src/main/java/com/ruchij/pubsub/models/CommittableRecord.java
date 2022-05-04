package com.ruchij.pubsub.models;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public record CommittableRecord<A>(A data, Supplier<CompletableFuture<Void>> commitData) {
}
