package de.effms.wamp.server.converter;

import de.effms.wamp.server.Websocket;
import de.effms.wamp.server.message.Message;

public interface Converter<T extends Message>
{
    public boolean canConvert(int messageCode);

    public String serialize(T message, Websocket socket) throws InvalidMessageCodeException;

    public T deserialize(String message, Websocket socket) throws MessageParseException, InvalidMessageCodeException;
}
