package com.ruchij.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class JsonObjectMapper {
    public static final ObjectMapper objectMapper =
            new ObjectMapper().registerModule(new JodaModule());

}
