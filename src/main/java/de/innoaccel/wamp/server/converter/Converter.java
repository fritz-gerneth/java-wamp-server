package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;

public interface Converter<T extends Message>
{
    public boolean canConvert(int messageCode);

    public String serialize(T message, Websocket socket) throws InvalidMessageCodeException;

    public T deserialize(String message, Websocket socket) throws MessageParseException, InvalidMessageCodeException;
}
