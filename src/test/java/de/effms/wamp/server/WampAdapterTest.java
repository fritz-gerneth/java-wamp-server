package de.effms.wamp.server;

import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.converter.Converter;
import de.effms.wamp.core.converter.InvalidMessageCodeException;
import de.effms.wamp.core.converter.MessageParseException;
import de.effms.wamp.core.dispatcher.DispatcherInterface;
import de.effms.wamp.core.message.Message;
import mockit.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Map;

public class WampAdapterTest
{
    private WampAdapter adapter;

    @Mocked
    private Converter messageConverter;

    @Mocked
    private DispatcherInterface messageDispatcher;

    @Mocked
    private Map<String, Websocket> socketStore;

    @Before
    public void setUp()
    {
        this.adapter = new WampAdapter(this.messageConverter, this.messageDispatcher, "'test-server", this.socketStore);
    }

    @Test
    public void afterConnectionEstablishedSavesNewAdapterInStore(final WebSocketSession session)
        throws InvalidMessageCodeException, IOException
    {
        new NonStrictExpectations() {{
            WampAdapterTest.this.messageConverter.serialize((Message) any, (Websocket) any); result = "serialized";
            session.getId(); result = "sessionId";
        }};

        this.adapter.afterConnectionEstablished(session);

        new Verifications() {{
            WampAdapterTest.this.socketStore.put("sessionId", (Websocket) any);
        }};
    }

    @Test
    public void sendsWelcomeMessageWithSessionIdAfterEstablishedConnection(final WebSocketSession session) throws InvalidMessageCodeException, IOException
    {
        new NonStrictExpectations() {{
            session.getId(); result = "sessionId";
            WampAdapterTest.this.messageConverter.serialize(withInstanceOf(Message.class), withInstanceOf(Websocket.class)); result = "serialized";
        }};

        this.adapter.afterConnectionEstablished(session);

        new Verifications() {{
            session.sendMessage(withInstanceOf(TextMessage.class));
        }};
    }

    @Test
    public void handleTextMessageDelegatesMarshaledMessageToDispatcher(
        final WebSocketSession session, final TextMessage sourceMessage, final Message message
    )
        throws InvalidMessageCodeException, MessageParseException, IOException
    {
        new NonStrictExpectations() {
            Websocket socket;
            {
                session.getId(); result = "sessionId";
                WampAdapterTest.this.socketStore.get("sessionId"); result = socket;
                sourceMessage.getPayload(); result = "message";
                socket.deserializeMessage("message"); result = message;
            }
        };

        this.adapter.handleTextMessage(session,sourceMessage);

        new Verifications() {{
            WampAdapterTest.this.messageDispatcher.dispatch(message);
        }};
    }
}
