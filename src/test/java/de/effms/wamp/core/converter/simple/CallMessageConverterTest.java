package de.effms.wamp.core.converter.simple;

import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.converter.GeneralMessageTests;
import de.effms.wamp.core.converter.InvalidMessageCodeException;
import de.effms.wamp.core.converter.MessageParseException;
import de.effms.wamp.core.converter.simple.CallMessageConverter;
import de.effms.wamp.core.message.CallMessage;
import de.effms.wamp.core.message.Message;
import junit.framework.Assert;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class CallMessageConverterTest extends GeneralMessageTests<CallMessage>
{
    @Before
    public void setUp()
    {
        this.converter = new CallMessageConverter();
    }

    @Test
    public void canConvertCallMessages()
    {
        Assert.assertTrue(this.converter.canConvert(Message.CALL));
    }

    @Test
    public void canNotConvertOtherMessages()
    {
        Assert.assertFalse(this.converter.canConvert(Message.INVALID));
    }

    @Test
    public void serializeUsesValuesOfMessage(final CallMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        this.converter.serialize(message, socket);

        new Verifications() {{
            message.getCallId();
            message.getProcURI();
            message.getArgs();
        }};
    }

    @Test
    public void serializeReturnsValidSerializedMessageWithNoArgs(final CallMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getCallId(); result = "callId";
            message.getProcURI(); result = "procURI";
            message.getArgs(); result = new ArrayList<Object>();
        }};

        Assert.assertEquals(
            "[" + Message.CALL + ",\"callId\",\"procURI\"]",
            this.converter.serialize(message, socket)
        );
    }

    @Test
    public void serializeReturnsValidSerializedMessageWithArg(final CallMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getCallId(); result = "callId";
            message.getProcURI(); result = "procURI";
            message.getArgs(); result = new ArrayList<Object>() {{ add("String"); }};
        }};

        Assert.assertEquals(
            "[" + Message.CALL + ",\"callId\",\"procURI\",\"String\"]",
            this.converter.serialize(message, socket)
        );
    }

    @Test
    public void serializeReturnsValidSerializedMessageWithManyMixedArgs(final CallMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getCallId(); result = "callId";
            message.getProcURI(); result = "procURI";
            message.getArgs(); result = new ArrayList<Object>() {{ add("String"); add(null); add(54); }};
        }};

        Assert.assertEquals(
            "[" + Message.CALL + ",\"callId\",\"procURI\",\"String\",null,54]",
            this.converter.serialize(message, socket)
        );
    }

    @Test(expected = MessageParseException.class)
    public void deserializeMessageRequiresAtLeastThreeFields(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.CALL + ", \"callId\"]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeMessageThrowsExceptionWhenCallIdIsNoString(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.CALL + ", null, \"procURI\"]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeMessageThrowsExceptionWhenProcURIIsNoString(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.CALL + ", \"callId\", null]", socket);
    }

    @Test
    public void deserializeMessageAdditionalFieldsAreCallArgs(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        CallMessage message = this.converter.deserialize(
            "[" + Message.CALL + ", \"callId\", \"procId\", null, 5]",
            socket
        );

        Assert.assertEquals(2, message.getArgs().size());
    }

    @Test
    public void deserializeProcURIIsInflatedBySocket(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.CALL + ", \"callId\", \"procCURI\"]", socket);

        new Verifications() {{
            socket.inflateCURI("procCURI");
        }};
    }

    @Test
    public void deserializedMessageHasCallIdOfInputMessage(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        CallMessage message = this.converter.deserialize(
            "[" + Message.CALL + ", \"callId\", \"procId\", null, 5]",
            socket
        );

        Assert.assertEquals("callId", message.getCallId());
    }

    @Test
    public void deserializedMessageHasInflatedProcURI(final Websocket socket)
        throws MessageParseException, InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            socket.inflateCURI("procId"); result = "http://effms.de/procId";
        }};

        CallMessage message = this.converter.deserialize(
            "[" + Message.CALL + ", \"callId\", \"procId\", null, 5]",
            socket
        );

        Assert.assertEquals("http://effms.de/procId", message.getProcURI());
    }
}
