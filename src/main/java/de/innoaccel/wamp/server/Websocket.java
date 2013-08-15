package de.innoaccel.wamp.server;

import de.innoaccel.wamp.server.converter.Converter;
import de.innoaccel.wamp.server.converter.InvalidMessageCode;
import de.innoaccel.wamp.server.converter.MessageParseError;
import de.innoaccel.wamp.server.message.Message;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class Websocket
{
    private final WebSocketSession session;

    private final Converter messageConverter;

    public Websocket(WebSocketSession session, Converter messageConverter)
    {
        this.session = session;
        this.messageConverter = messageConverter;
    }

    public String getSessionId()
    {
        return this.session.getId();
    }

    public void sendMessage(Message message) throws InvalidMessageCode, IOException
    {
        this.session.sendMessage(new TextMessage(this.messageConverter.serialize(message, this)));
    }

    public Message deserializeMessage(String rawMessage) throws InvalidMessageCode, MessageParseError
    {
       return this.messageConverter.deserialize(rawMessage, this);
    }
}
