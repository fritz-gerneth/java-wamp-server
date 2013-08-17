package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.Message;
import de.innoaccel.wamp.server.message.PrefixMessage;
import mockit.Expectations;
import mockit.NonStrictExpectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
        throws InvalidMessageCode
    {
        new Expectations() {{
            message.getPrefix(); result = "myPrefix";
            message.getURI(); result = "http://innoaccel.de/myPrefix";
        }};

        this.converter.serialize(message, socket);
    }

    @Test
    public void serialzeMessageReturnsValidStringRepresentation(final PrefixMessage message, Websocket socket)
        throws InvalidMessageCode
    {
        new NonStrictExpectations() {{
            message.getPrefix(); result = "myPrefix";
            message.getURI(); result = "http://innoaccel.de/myPrefix";
        }};

        Assert.assertEquals(
            "[1, \"myPrefix\", \"http://innoaccel.de/myPrefix\"]",
            this.converter.serialize(message, socket)
        );
    }
}
