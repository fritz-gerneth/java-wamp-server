package de.effms.wamp.core.converter;

import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.message.Message;
import de.effms.wamp.core.message.SubscribeMessage;
import junit.framework.Assert;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

public class SubscribeMessageConverterTest extends GeneralMessageTests<SubscribeMessage>
{
    @Before
    public void setUp()
    {
        this.converter = new SubscribeMessageConverter();
    }

    @Test
    public void canConvertSubscribeMessages()
    {
        Assert.assertTrue(this.converter.canConvert(Message.SUBSCRIBE));
    }

    @Test
    public void canNotConvertOtherMessages()
    {
        Assert.assertFalse(this.converter.canConvert(Message.INVALID));
    }

    @Test
    public void serializeUsesTopicURIOfMessage(final SubscribeMessage message, final Websocket socket) throws InvalidMessageCodeException
    {
        this.converter.serialize(message, socket);

        new Verifications() {{
            message.getTopicURI();
        }};
    }

    @Test
    public void serializeReturnsStringRepresentationOfMessage(final SubscribeMessage message, final Websocket socket) throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getTopicURI(); result = "myTopic";
        }};

        Assert.assertEquals(
            "[" + Message.SUBSCRIBE + ",\"myTopic\"]",
            this.converter.serialize(message, socket)
        );
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenTopicFieldIsNotPresent(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize("[" + Message.SUBSCRIBE + "]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenTopicFieldIsEmpty(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize("[" + Message.SUBSCRIBE + ", \"\"]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenTopicFieldIsNoString(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize("[" + Message.SUBSCRIBE + ", null]", socket);
    }

    @Test
    public void deserializeInflatesCURIByDelegatingToSocket(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize("[" + Message.SUBSCRIBE + ", \"curi\"]", socket);

        new Verifications() {{
            socket.inflateCURI("curi");
        }};
    }

    @Test
    public void deserializeResultingMessageHasTopicOfMessage(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        new NonStrictExpectations() {{
            socket.inflateCURI("curi"); result = "http://effms.de/curi";
        }};
        SubscribeMessage message = this.converter.deserialize("[" + Message.SUBSCRIBE + ", \"curi\"]", socket);

        Assert.assertEquals("http://effms.de/curi", message.getTopicURI());
    }
}
