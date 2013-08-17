package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.PrefixMessage;

public class PrefixMessageConverter implements Converter
{
    @Override
    public boolean canConvert(int messageCode)
    {
        return Message.PREFIX == messageCode;
    }

    @Override
    public String serialize(Message message, Websocket socket) throws InvalidMessageCode
    {
        PrefixMessage msg = (PrefixMessage) message;
        return "[" + Message.PREFIX + ", \"" + msg.getPrefix() + "\", \"" + msg.getURI() + "\"]";
    }

    @Override
    public Message deserialize(String message, Websocket socket) throws MessageParseError, InvalidMessageCode
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}