package de.effms.wamp.core.converter;

import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.message.Message;
import de.effms.wamp.core.message.UnsubscribeMessage;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class UnsubscribeMessageConverterTest extends GeneralMessageTests<UnsubscribeMessage>
{
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
