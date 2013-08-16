package de.innoaccel.wamp.server;

import de.innoaccel.wamp.server.converter.Converter;
import de.innoaccel.wamp.server.converter.InvalidMessageCode;
import de.innoaccel.wamp.server.converter.MessageParseError;
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
import java.util.Map;

public class WebsocketTest
{
    @Mocked
    private WebSocketSession session;

    @Mocked
    private Converter messageConverter;

    @Mocked
    private Map<String, String> prefixMap;

    private Websocket socket;

    @Before
    public void setUp()
    {
        this.socket = new Websocket(this.session, this.messageConverter, this.prefixMap);
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

    @Test
    public void deserializeMessageDelegatesToMessageConverter() throws InvalidMessageCode, MessageParseError
    {
        this.socket.deserializeMessage("message");

        new Verifications() {{
            WebsocketTest.this.messageConverter.deserialize("message", (Websocket) any);
        }};
    }

    @Test
    public void deserializeMessageIsDoneWithWebsocketContext() throws InvalidMessageCode, MessageParseError
    {
        this.socket.deserializeMessage("message");

        new Verifications() {{
            WebsocketTest.this.messageConverter.deserialize(anyString, withSameInstance(WebsocketTest.this.socket));
        }};
    }

    @Test
    public void getPrefixMapReturnsInstanceOfConstruction()
    {
        Assert.assertSame(this.prefixMap, this.socket.getPrefixMap());
    }

    @Test
    public void addPrefixAddsToPrefixMap()
    {
        new Expectations() {{
            WebsocketTest.this.prefixMap.put("prefix", "fullURI"); times = 1;
        }};

        this.socket.addPrefix("prefix", "fullURI");
    }
}
