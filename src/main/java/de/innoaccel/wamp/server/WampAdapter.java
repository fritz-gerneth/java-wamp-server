package de.innoaccel.wamp.server;

import de.innoaccel.wamp.server.converter.Converter;
import de.innoaccel.wamp.server.converter.InvalidMessageCodeException;
import de.innoaccel.wamp.server.converter.MessageParseException;
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

    private final Map<String, Websocket> socketStore;

    private final Converter messageConverter;

    public WampAdapter(Converter messageConverter)
    {
        this(messageConverter, new HashMap<String, Websocket>());
    }

    public WampAdapter(Converter messageConverter, Map<String, Websocket> socketStore)
    {
        this.socketStore = socketStore;
        this.messageConverter = messageConverter;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage rawMessage) throws InvalidMessageCodeException, MessageParseException, IOException
    {
        Websocket socket = this.socketStore.get(session.getId());
        Message message = socket.deserializeMessage(rawMessage.getPayload());
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws InvalidMessageCodeException, IOException
    {
        Websocket socket = new Websocket(session, this.messageConverter);
        this.socketStore.put(session.getId(), socket);
        socket.sendMessage(new WelcomeMessage());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws InvalidMessageCodeException, IOException
    {
        this.socketStore.remove(session.getId());
    }
}
