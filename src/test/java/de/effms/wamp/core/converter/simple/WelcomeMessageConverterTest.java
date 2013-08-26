package de.effms.wamp.core.converter.simple;

import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.converter.GeneralMessageTests;
import de.effms.wamp.core.converter.InvalidMessageCodeException;
import de.effms.wamp.core.converter.MessageParseException;
import de.effms.wamp.core.converter.simple.WelcomeMessageConverter;
import de.effms.wamp.core.message.Message;
import de.effms.wamp.core.message.WelcomeMessage;
import mockit.Expectations;
import mockit.NonStrictExpectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WelcomeMessageConverterTest extends GeneralMessageTests<WelcomeMessage>
{
    @Before
    public void setUp()
    {
        this.converter = new WelcomeMessageConverter();
    }

    @Test
    public void canConvertMessagesWithWelcomeMessageCode()
    {
        Assert.assertTrue(this.converter.canConvert(Message.WELCOME));
    }

    @Test
    public void canNotConvertOtherMessageTypes()
    {
        Assert.assertFalse(this.converter.canConvert(Message.INVALID));
    }

    @Test
    public void serializeWelcomeMessageUsesSessionIdOfSocket(final WelcomeMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new Expectations()
        {{
            socket.getSessionId(); result = "sessionId";
        }};
        new NonStrictExpectations() {{
            message.getProtocolVersion(); result = 1;
            message.getServerIdent(); result = "ident";
        }};

        Assert.assertEquals("[0,\"sessionId\",1,\"ident\"]", this.converter.serialize(message, socket));
    }

    @Test
    public void serializeUsesValuesOfWelcomeMessage(final WelcomeMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new Expectations()
        {{
            message.getProtocolVersion(); result = 1;
            message.getServerIdent(); result = "ident";
        }};
        new NonStrictExpectations() {{
            socket.getSessionId(); result = "sessionId";
        }};

        Assert.assertEquals("[0,\"sessionId\",1,\"ident\"]", this.converter.serialize(message, socket));
    }

    @Test(expected = MessageParseException.class)
    public void deserializeExceptionOnMissingSessionField(final Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.WELCOME + "]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeExceptionWhenSessionFieldIsNoString(final Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.WELCOME + ", null]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeExceptionOnMissingVersionField(final Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.WELCOME + ", \"id\"]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeExceptionWhenVersionFieldIsNoInteger(final Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.WELCOME + ", , \"id\", null]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeExceptionOnMissingIdentField(final Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.WELCOME + ", \"id\", 1]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeExceptionWhenIdentFieldIsNoString(final Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.WELCOME + ", , \"id\", 1, null]", socket);
    }

    @Test
    public void deserializeReturnsPopulatedWelcomeMessage(final Websocket socket) throws MessageParseException, InvalidMessageCodeException
    {
        WelcomeMessage message = this.converter.deserialize("[0, \"mySessionId\", 1, \"serverIdent\"]", socket);

        Assert.assertEquals("mySessionId", message.getSessionId());
        Assert.assertEquals(1, message.getProtocolVersion());
        Assert.assertEquals("serverIdent", message.getServerIdent());
    }
}
