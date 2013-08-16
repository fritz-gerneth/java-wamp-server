package de.innoaccel.wamp.server;

import de.innoaccel.wamp.server.converter.Converter;
import de.innoaccel.wamp.server.converter.InvalidMessageCode;
import de.innoaccel.wamp.server.converter.MessageParseError;
import de.innoaccel.wamp.server.message.Message;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Websocket
{
    private final WebSocketSession session;

    private final Converter messageConverter;

    private final Map<String, String> prefixMap;

    public Websocket(WebSocketSession session, Converter messageConverter)
    {
        this(session, messageConverter, new HashMap<String, String>());
    }

    public Websocket(WebSocketSession session, Converter messageConverter, Map<String, String> prefixMap)
    {
        this.session = session;
        this.messageConverter = messageConverter;
        this.prefixMap = prefixMap;
    }

    public String getSessionId()
    {
        return this.session.getId();
    }

    public Map<String, String> getPrefixMap()
    {
        return this.prefixMap;
    }

    public void addPrefix(String prefix, String fullURI)
    {
        this.prefixMap.put(prefix, fullURI);
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
