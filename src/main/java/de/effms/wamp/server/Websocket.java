package de.effms.wamp.server;

import de.effms.wamp.server.converter.Converter;
import de.effms.wamp.server.converter.InvalidMessageCodeException;
import de.effms.wamp.server.converter.MessageParseException;
import de.effms.wamp.server.message.Message;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Websocket
{
    private final WebSocketSession session;

    private final Converter messageConverter;

    private final Map<String, String> prefixMap;

    private final static Pattern prefixPattern = Pattern.compile("^(?<prefix>[^:]+):(?<reference>.+)");

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

    public void sendMessage(Message message) throws InvalidMessageCodeException, IOException
    {
        this.session.sendMessage(new TextMessage(this.messageConverter.serialize(message, this)));
    }

    public Message deserializeMessage(String rawMessage) throws InvalidMessageCodeException, MessageParseException
    {
       return this.messageConverter.deserialize(rawMessage, this);
    }

    public String inflateCURI(String curi)
    {
        if (curi.substring(0, 7).equals("http://")) {
            return curi;
        }

        Matcher elements = Websocket.prefixPattern.matcher(curi);
        if (!elements.matches()) {
            return curi;
        }

        String prefix = elements.group("prefix");
        if (!this.prefixMap.containsKey(prefix)) {
            return curi;
        }

        return this.prefixMap.get(prefix) + elements.group("reference");
    }
}
