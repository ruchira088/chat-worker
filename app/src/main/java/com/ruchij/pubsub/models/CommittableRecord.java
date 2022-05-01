package com.ruchij.pubsub.models;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public record CommittableRecord<A>(A data, Callable<CompletableFuture<Void>> commitData) {
}
