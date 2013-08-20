package de.innoaccel.wamp.server.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.innoaccel.wamp.server.message.Message;

abstract public class JsonParsingConverter<T extends Message> implements Converter<T>
{
    protected final ObjectMapper objectMapper;

    public JsonParsingConverter(ObjectMapper objectMapper)
    {
        this.objectMapper = objectMapper;
    }
}
