package com.nettrack.common.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import org.springframework.util.StringUtils;

public class StringJsonDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser jsonParser, DeserializationContext ctx) throws IOException {
        String text = jsonParser.getText();
        return StringUtils.isEmpty(text) ? text : text.trim();
    }

}
