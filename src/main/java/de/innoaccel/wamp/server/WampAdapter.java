package de.innoaccel.wamp.server;

import de.innoaccel.wamp.server.converter.Converter;
import de.innoaccel.wamp.server.converter.InvalidMessageCode;
import de.innoaccel.wamp.server.converter.MessageParseError;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.WelcomeMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.TextWebSocketHandlerAdapter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WampAdapter extends TextWebSocketHandlerAdapter
{

    private final Map<String, Websocket> sessionMap;

    private final Converter messageConverter;

    public WampAdapter(Converter messageConverter)
    {
        this.sessionMap = new HashMap<String, Websocket>();
        this.messageConverter = messageConverter;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage rawMessage) throws InvalidMessageCode, MessageParseError, IOException
    {
        Websocket socket = this.sessionMap.get(session.getId());
        Message message = socket.deserializeMessage(rawMessage.getPayload());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws InvalidMessageCode, IOException
    {
        Websocket socket = new Websocket(session, this.messageConverter);

        this.sessionMap.put(session.getId(), socket);
        socket.sendMessage(new WelcomeMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws InvalidMessageCode, IOException
    {
        this.sessionMap.remove(session.getId());
    }
}
