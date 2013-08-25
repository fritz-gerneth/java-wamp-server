package de.effms.wamp.server;

import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.converter.Converter;
import de.effms.wamp.core.converter.InvalidMessageCodeException;
import de.effms.wamp.core.converter.MessageParseException;
import de.effms.wamp.core.dispatcher.DispatcherInterface;
import de.effms.wamp.core.message.Message;
import de.effms.wamp.core.message.WelcomeMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.adapter.TextWebSocketHandlerAdapter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WampAdapter extends TextWebSocketHandlerAdapter
{
    private final Converter messageConverter;

    private final DispatcherInterface messageDispatcher;

    private final Map<String, Websocket> socketStore;

    private final String serverIdent;

    public WampAdapter(Converter messageConverter, DispatcherInterface messageDispatcher, String serverIdent)
    {
        this(messageConverter, messageDispatcher, serverIdent, new HashMap<String, Websocket>());
    }

    public WampAdapter(Converter messageConverter, DispatcherInterface messageDispatcher, String serverIdent, Map<String, Websocket> socketStore)
    {
        this.messageConverter = messageConverter;
        this.messageDispatcher = messageDispatcher;
        this.serverIdent = serverIdent;
        this.socketStore = socketStore;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage rawMessage) throws InvalidMessageCodeException, MessageParseException, IOException
    {
        Websocket socket = this.socketStore.get(session.getId());
        Message message = socket.deserializeMessage(rawMessage.getPayload());
        this.messageDispatcher.dispatch(message, socket);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws InvalidMessageCodeException, IOException
    {
        Websocket socket = new Websocket(session, this.messageConverter);
        this.socketStore.put(session.getId(), socket);
        socket.sendMessage(new WelcomeMessage(this.serverIdent));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws InvalidMessageCodeException, IOException
    {
        this.socketStore.remove(session.getId());
    }
}
