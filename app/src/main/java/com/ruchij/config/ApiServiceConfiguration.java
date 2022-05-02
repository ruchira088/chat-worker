package com.ruchij.config;

import java.net.URI;

public record ApiServiceConfiguration(URI serviceUrl, String authenticationToken) {
}
