package de.effms.wamp.server.converter;

import de.effms.wamp.server.Websocket;
import de.effms.wamp.server.message.CallResultMessage;
import de.effms.wamp.server.message.Message;
import junit.framework.Assert;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

public class CallResultMessageConverterTest extends GeneralMessageTests<CallResultMessage>
{
    @Before
    public void setUp()
    {
        this.converter = new CallResultMessageConverter();
    }

    @Test
    public void canConvertCallResultMessages()
    {
        Assert.assertTrue(this.converter.canConvert(Message.CALL_RESULT));
    }

    @Test
    public void canNotConvertOtherMessages()
    {
        Assert.assertFalse(this.converter.canConvert(Message.INVALID));
    }

    @Test
    public void serializeUsesValuesOfMessage(final CallResultMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        this.converter.serialize(message, socket);

        new Verifications() {{
            message.getCallId();
            message.getResult();
        }};
    }

    @Test
    public void serializeReturnsCorrectlySerializedMessage(final CallResultMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getCallId(); result = "callId";
            message.getResult(); result = 5;
        }};

        Assert.assertEquals(
            "[" + Message.CALL_RESULT + ",\"callId\",5]",
            this.converter.serialize(message, socket)
        );
    }

    @Test(expected = MessageParseException.class)
    public void deserializeExpectsThreeFieldsInMessage(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize("[" + Message.CALL_RESULT + ", \"callId\"]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionIfCallIdFieldIsNoString(final Websocket socket)
    throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize("[" + Message.CALL_RESULT + ", null, \"result\"]", socket);
    }

    @Test
    public void deserializedMessageHasCallIdOfInput(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        CallResultMessage message = this.converter.deserialize(
            "[" + Message.CALL_RESULT + ", \"callId\", \"result\"]",
            socket
        );

        Assert.assertEquals("callId", message.getCallId());
    }

    @Test
    public void deserializedMessageResultIsAlwaysSet(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        CallResultMessage message = this.converter.deserialize(
            "[" + Message.CALL_RESULT + ", \"callId\", null]",
            socket
        );

        Assert.assertNotNull(message.getResult());
    }
}
