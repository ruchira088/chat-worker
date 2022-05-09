package com.ruchij.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.ruchij.json.JsonObjectMapper.objectMapper;
import static java.net.http.HttpResponse.BodySubscribers;

public class JsonBody {

    public static <T> HttpResponse.BodyHandler<T> jsonBodyHandler(Class<T> clazz) {
        return jsonBodyHandler(clazz, objectMapper);
    }

    public static <T> HttpResponse.BodyHandler<T> jsonBodyHandler(Class<T> clazz, ObjectMapper objectMapper) {
        return responseInfo -> BodySubscribers.mapping(
            BodySubscribers.ofInputStream(),
            inputStream -> {
                try {
                    return objectMapper.readValue(inputStream, clazz);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        );
    }

    public static HttpRequest.BodyPublisher jsonBodyPublisher(Object data) {
        return jsonBodyPublisher(data, objectMapper);
    }

    public static HttpRequest.BodyPublisher jsonBodyPublisher(Object data, ObjectMapper objectMapper) {
        byte[] bytes;

        try {
            bytes = objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return HttpRequest.BodyPublishers.ofByteArray(bytes);
    }
}
