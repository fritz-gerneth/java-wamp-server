package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.UnsubscribeMessage;
import mockit.Expectations;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
    public void serializeUsesTopicURLOfMessage(final UnsubscribeMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        new Expectations() {{
            message.getTopicURI(); result = "http://innoaccel";
        }};

        Assert.assertTrue(this.converter.serialize(message, socket).contains("http://innoaccel"));
    }

    @Test
    public void serializeReturnsValidMarkup(final UnsubscribeMessage message, Websocket socket) throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getTopicURI(); result = "http://innoaccel";
        }};

        Assert.assertTrue(this.converter.serialize(message, socket).matches("\\[(\\d+),\\s\"(.+?)\"\\]"));
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionOnInvalidMessageMarkup(Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[invalid", socket);
    }

    @Test(expected = InvalidMessageCodeException.class)
    public void deserializeThrowsExceptionOnInvalidMessageCode(Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[4, \"x\"]", socket);
    }

    @Test
    public void deserializeAcceptsCorrectMessageType(Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[6, \"x\"]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeURIFragmentRequired(Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[6, \"\"]", socket);
    }

    @Test
    public void deserializeDelegatesURIInflationToSocket(final Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[6, \"x\"]", socket);

        new Verifications() {{
            socket.inflateCURI("x");
        }};
    }
}
