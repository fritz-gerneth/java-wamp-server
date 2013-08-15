package de.innoaccel.wamp.server;

import de.innoaccel.wamp.server.converter.Converter;
import de.innoaccel.wamp.server.converter.InvalidMessageCode;
import de.innoaccel.wamp.server.message.Message;
import junit.framework.Assert;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public class WebsocketTest
{
    @Mocked
    private WebSocketSession session;

    @Mocked
    private Converter messageConverter;

    private Websocket socket;

    @Before
    public void setUp()
    {
        this.socket = new Websocket(this.session, this.messageConverter);
    }

    @Test
    public void hasSessionIdOfUnderlyingConnection()
    {
        new Expectations() {{
            WebsocketTest.this.session.getId(); result = "WebSocketId";
        }};

        Assert.assertEquals("WebSocketId", this.socket.getSessionId());
    }

    @Test
    public void sendMessagesDelegatesConverterResultToUnderlyingConnection(final Message message) throws InvalidMessageCode, IOException
    {
        new Expectations() {{
            WebsocketTest.this.messageConverter.serialize(message, WebsocketTest.this.socket); result = "serializedMessage";
        }};

        this.socket.sendMessage(message);

        new Verifications() {{
            WebSocketMessage m;
            WebsocketTest.this.session.sendMessage(m = withCapture());
            Assert.assertEquals("serializedMessage", m.getPayload());
        }};
    }
}
