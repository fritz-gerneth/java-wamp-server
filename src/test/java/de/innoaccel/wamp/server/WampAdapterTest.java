package de.innoaccel.wamp.server;

import de.innoaccel.wamp.server.converter.Converter;
import de.innoaccel.wamp.server.converter.InvalidMessageCode;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.WelcomeMessage;
import mockit.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WampAdapterTest
{
    private WampAdapter adapter;

    @Mocked
    private Converter messageConverter;

    @Before
    public void setUp()
    {
        this.adapter = new WampAdapter(this.messageConverter);
    }

    @Test
    public void sendsWelcomeMessageWithSessionIdAfterEstablishedConnection(final WebSocketSession session) throws InvalidMessageCode, IOException
    {
        new MockUp<Websocket>() {
            @Mock
            public void sendMessage(Message message)
            {
                Assert.isInstanceOf(WelcomeMessage.class, message);
            }
        };
        new NonStrictExpectations() {{
            session.getId(); result = "sessionId";
        }};

        this.adapter.afterConnectionEstablished(session);
    }
}
