package de.innoaccel.wamp.server;

import de.innoaccel.wamp.server.converter.Converter;
import de.innoaccel.wamp.server.converter.InvalidMessageCode;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.WelcomeMessage;
import junit.framework.Assert;
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
    private Map<String, Websocket> socketStore;

    @Before
    public void setUp()
    {
        this.adapter = new WampAdapter(this.messageConverter, this.socketStore);
    }

    @Test
    public void afterConnectionEstablishedSavesNewAdapterInStore(final WebSocketSession session)
        throws InvalidMessageCode, IOException
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
    public void sendsWelcomeMessageWithSessionIdAfterEstablishedConnection(final WebSocketSession session) throws InvalidMessageCode, IOException
    {
        new NonStrictExpectations() {{
            session.getId(); result = "sessionId";
            WampAdapterTest.this.messageConverter.serialize((Message) any, (Websocket) any); result = "serialized";
        }};

        this.adapter.afterConnectionEstablished(session);

        new Verifications() {{
            session.sendMessage((TextMessage) any);
        }};
    }
}
