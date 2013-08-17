package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.UnsubscribeMessage;

public class UnsubscribeMessageConverter implements Converter<UnsubscribeMessage>
{
    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.UNSUBSCRIBE == messageCode;
    }

    @Override
    public String serialize(UnsubscribeMessage message, Websocket socket) throws InvalidMessageCode
    {
        return "[" + Message.UNSUBSCRIBE + ", \"" + message.getTopicURI() + "\"]";
    }

    @Override
    public UnsubscribeMessage deserialize(String message, Websocket socket) throws MessageParseError, InvalidMessageCode
    {
        return null;
    }
}
