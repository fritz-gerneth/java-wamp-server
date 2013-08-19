package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.EventMessage;
import de.innoaccel.wamp.server.message.Message;

public class EventMessageConverter implements Converter<EventMessage>
{
    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.EVENT == messageCode;
    }

    @Override
    public String serialize(EventMessage message, Websocket socket) throws InvalidMessageCode
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public EventMessage deserialize(String message, Websocket socket) throws MessageParseError, InvalidMessageCode
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
