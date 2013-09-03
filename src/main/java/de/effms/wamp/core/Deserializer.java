package de.effms.wamp.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.effms.wamp.core.resolver.MessageTypeResolver;

import java.io.IOException;

public class Deserializer
{
    private final ObjectMapper objectMapper;

    private final MessageTypeResolver typeResolver;

    public Deserializer(ObjectMapper objectMapper, MessageTypeResolver typeResolver)
    {
        this.objectMapper = objectMapper;
        this.typeResolver = typeResolver;
    }

    public Object deserialize(String message, Websocket socket) throws IOException
    {
         return this.objectMapper.readValue(message, this.typeResolver.resolve(message, socket));
    }
}
