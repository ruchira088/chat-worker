package com.ruchij.config;

import com.typesafe.config.Config;

import java.net.URI;

public record ApiServiceConfiguration(URI serviceUrl, String authenticationToken) {

    public static ApiServiceConfiguration fromConfig(Config config) {
        Config apiServiceConfig = config.getConfig("api-service-configuration");

        URI serviceUrl = URI.create(apiServiceConfig.getString("service-url"));
        String authenticationToken = apiServiceConfig.getString("authentication-token");

        return new ApiServiceConfiguration(serviceUrl, authenticationToken);
    }

}
