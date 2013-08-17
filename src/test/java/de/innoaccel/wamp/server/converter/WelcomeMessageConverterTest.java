package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.WelcomeMessage;
import mockit.Expectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class WelcomeMessageConverterTest
{
    private WelcomeMessageConverter converter;

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
        Assert.assertFalse(this.converter.canConvert(54));
    }

    @Test
    public void serializeWelcomeMessageUsesSessionIdOfSocket(final WelcomeMessage message, final Websocket socket)
            throws InvalidMessageCode
    {
        new Expectations()
        {{
            socket.getSessionId(); result = "sessionId";
        }};

        Assert.assertEquals(this.converter.serialize(message, socket), "[0, \"sessionId\", 1, \"\"]");
    }

    @Test(expected = MessageParseError.class)
    public void deserializeThrowsExceptionForWrongSyntax(final Websocket socket) throws MessageParseError, InvalidMessageCode
    {
        this.converter.deserialize("[dd", socket);
    }

    @Test(expected = InvalidMessageCode.class)
    public void deserializeThrowsExceptionForWrongCode(final Websocket socket) throws MessageParseError, InvalidMessageCode
    {
        this.converter.deserialize("[4, \"x\", 1 ,\"x\"]", socket);
    }

    @Test
    public void deserializeReturnsPopulatedWelcomeMessage(final Websocket socket) throws MessageParseError, InvalidMessageCode
    {
        WelcomeMessage message = this.converter.deserialize("[0, \"mySessionId\", 1, \"serverIdent\"]", socket);

        Assert.assertEquals("mySessionId", message.getSessionId());
        Assert.assertEquals(1, message.getProtocolVersion());
        Assert.assertEquals("serverIdent", message.getServerIdent());
    }
}
