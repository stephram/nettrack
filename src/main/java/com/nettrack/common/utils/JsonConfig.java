package com.nettrack.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JsonConfig {

    @Primary
    @Bean(name = {"fasterXmlObjectMapper"})
    public ObjectMapper objectMapper() {
        return JsonMapperFactory.objectMapper();
    }

    @Bean(name = {"objectMapperUnquotedFieldNames"})
    public ObjectMapper objectMapperUnquotedFieldNames() {
        return JsonMapperFactory.objectMapperUnquotedFieldNames();
    }
}
