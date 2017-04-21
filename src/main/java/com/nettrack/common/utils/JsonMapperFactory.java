package com.nettrack.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Created by sgraham on 17/4/17.
 */
public final class JsonMapperFactory {

    private JsonMapperFactory() {
    }

    public static ObjectMapper objectMapper() {
        final SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new StringJsonDeserializer());

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(module);
        objectMapper.registerModule(new JavaTimeModule());

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }

    public static ObjectMapper objectMapperUnquotedFieldNames() {
        final ObjectMapper objectMapper = objectMapper();
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        return objectMapper;
    }
}
