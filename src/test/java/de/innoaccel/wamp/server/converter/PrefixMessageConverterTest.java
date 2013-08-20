package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.PrefixMessage;
import mockit.Expectations;
import mockit.NonStrictExpectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class PrefixMessageConverterTest
{
    private PrefixMessageConverter converter;

    @Before
    public void setUp()
    {
        this.converter = new PrefixMessageConverter();
    }

    @Test
    public void canConvertPrefixMessages()
    {
        Assert.assertTrue(this.converter.canConvert(Message.PREFIX));
    }

    @Test
    public void canNotConvertOtherMessages()
    {
        Assert.assertFalse(this.converter.canConvert(Message.WELCOME));
    }

    @Test
    public void serializesMessagesUsesPrefixAndURIOfMessage(final PrefixMessage message, Websocket socket)
        throws InvalidMessageCodeException
    {
        new Expectations() {{
            message.getPrefix(); result = "myPrefix";
            message.getURI(); result = "http://innoaccel.de/myPrefix";
        }};

        this.converter.serialize(message, socket);
    }

    @Test
    public void serializeMessageReturnsValidStringRepresentation(final PrefixMessage message, Websocket socket)
        throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getPrefix(); result = "myPrefix";
            message.getURI(); result = "http://innoaccel.de/myPrefix";
        }};

        Assert.assertEquals(
            "[1,\"myPrefix\",\"http://innoaccel.de/myPrefix\"]",
            this.converter.serialize(message, socket)
        );
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenMessageIsNoArray(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("message", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenThereIsNoFirstField(final Websocket socket)
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
    public void deserializeThrowsMessageParseExceptionWhenPrefixFieldIsNotPresent(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PREFIX + "]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenPrefixFieldIsEmpty(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PREFIX + ", \"\"]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenURIFieldIsNotPresent(Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PREFIX + ", \"prefix\"]", socket);;
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenURIFieldIsEmpty(Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.PREFIX + ", \"prefix\", \"\"]", socket);
    }

    @Test
    public void deserializeReturnsPrefixMessageWithPrefixOfMessage(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        PrefixMessage message = this.converter.deserialize("[" + Message.PREFIX + ", \"prefix\", \"URI\"]", socket);

        Assert.assertEquals("prefix", message.getPrefix());
    }

    @Test
    public void deserializeReturnsPrefixMessageWithURIOfMessage(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        PrefixMessage message = this.converter.deserialize("[" + Message.PREFIX + ", \"prefix\", \"URI\"]", socket);

        Assert.assertEquals("URI", message.getURI());
    }
}
