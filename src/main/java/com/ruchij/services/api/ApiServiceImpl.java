package com.ruchij.services.api;

import com.google.common.net.HttpHeaders;
import com.ruchij.config.ApiServiceConfiguration;
import com.ruchij.json.JsonBody;
import com.ruchij.services.messages.models.OneToOne;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ApiServiceImpl implements ApiService {
    private final HttpClient httpClient;
    private final ApiServiceConfiguration apiServiceConfiguration;

    public ApiServiceImpl(HttpClient httpClient, ApiServiceConfiguration apiServiceConfiguration) {
        this.httpClient = httpClient;
        this.apiServiceConfiguration = apiServiceConfiguration;
    }

    @Override
    public CompletableFuture<Boolean> deliver(OneToOne oneToOne) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(apiServiceConfiguration.serviceUrl().resolve("/push"))
                .timeout(Duration.ofSeconds(5))
                .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(apiServiceConfiguration.authenticationToken()))
                .POST(JsonBody.jsonBodyPublisher(oneToOne))
                .build();

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.discarding())
                .thenApply(response -> response.statusCode() < 300);
    }
}
