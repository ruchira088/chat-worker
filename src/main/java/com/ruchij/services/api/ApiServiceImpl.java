package com.ruchij.services.api;

import com.google.common.net.HttpHeaders;
import com.ruchij.config.ApiServiceConfiguration;
import com.ruchij.json.JsonBody;
import com.ruchij.services.messages.models.OneToOne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class ApiServiceImpl implements ApiService {
    private final Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

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
            .version(HttpClient.Version.HTTP_1_1)
            .header(HttpHeaders.AUTHORIZATION, "Bearer %s".formatted(apiServiceConfiguration.authenticationToken()))
            .POST(JsonBody.jsonBodyPublisher(oneToOne))
            .build();

        logger.info("Sending message to PUSH messageId=%s".formatted(oneToOne.messageId()));

        return httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.discarding())
            .thenApply(response -> {
                logger.info("Response received for PUSH messageId=%s".formatted(oneToOne.messageId()));

                return response.statusCode() < 300;
            })
            .exceptionally(throwable -> {
                logger.error("Failed to PUSH messageId=%s".formatted(oneToOne.messageId()), throwable);
                return false;
            });
    }
}
