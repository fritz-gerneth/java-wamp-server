package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.UnsubscribeMessage;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class UnsubscribeMessageConverterTest
{
    private UnsubscribeMessageConverter converter;

    @Before
    public void setUp()
    {
        this.converter = new UnsubscribeMessageConverter();
    }

    @Test
    public void canConvertUnsubscribeMessages()
    {
        Assert.assertTrue(this.converter.canConvert(Message.UNSUBSCRIBE));
    }

    @Test
    public void canNotConvertOtherMessages()
    {
        Assert.assertFalse(this.converter.canConvert(Message.SUBSCRIBE));
    }

    @Test
    public void serializeReturnsMessageWithCorrectTopicURL(final UnsubscribeMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        new Expectations() {{
            message.getTopicURI(); result = "http://effms.de/";
        }};

        Assert.assertEquals("[" + Message.UNSUBSCRIBE + ",\"http://effms.de/\"]", this.converter.serialize(message, socket));
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenMessageIsNoArray(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("message", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenMessageCodeFieldIsNotPresent(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenFirstFieldIsNoNumber(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[null]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenFirstFieldIsNoInteger(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[0.5f]", socket);
    }

    @Test(expected = InvalidMessageCodeException.class)
    public void deserializeThrowsInvalidMessageCodeExceptionWhenWrongMessageCode(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.SUBSCRIBE + "]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenTopicFieldIsNotPresent(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.UNSUBSCRIBE + "]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenTopicFieldIsEmpty(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.UNSUBSCRIBE + ", \"\"]", socket);
    }

    @Test
    public void deserializeDelegatesURIInflationToSocket(final Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        new Expectations() {{
            socket.inflateCURI("x"); result = "http://effms.de/";
        }};
        this.converter.deserialize("[6, \"x\"]", socket);
    }

    @Test
    public void deserializedMessageHasTopicOfMessage(final Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        new Expectations() {{
            socket.inflateCURI(anyString); result = "http://effms.de/";
        }};
        UnsubscribeMessage message = this.converter.deserialize("[6, \"x\"]", socket);

        Assert.assertEquals("http://effms.de/", message.getTopicURI());
    }
}
