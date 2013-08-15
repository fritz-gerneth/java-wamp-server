package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;

public interface Converter
{
    public boolean canConvert(int messageCode);

    public String serialize(Message message, Websocket socket) throws InvalidMessageCode;

    public Message deserialize(String message, Websocket socket) throws MessageParseError, InvalidMessageCode;
}
