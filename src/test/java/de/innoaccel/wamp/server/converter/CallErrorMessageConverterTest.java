package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.CallErrorMessage;
import de.innoaccel.wamp.server.message.Message;
import junit.framework.Assert;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import org.junit.Before;
import org.junit.Test;

public class CallErrorMessageConverterTest extends GeneralMessageTests<CallErrorMessage>
{
    @Before
    public void setUp()
    {
        this.converter = new CallErrorMessageConverter();
    }

    @Test
    public void canConverterCallErrorMessages()
    {
        Assert.assertTrue(this.converter.canConvert(Message.CALL_ERROR));
    }

    @Test
    public void canNotConvertOtherMessages()
    {
        Assert.assertFalse(this.converter.canConvert(Message.INVALID));
    }

    @Test
    public void serializeUsesValuesOfMessage(final CallErrorMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        this.converter.serialize(message, socket);

        new Verifications() {{
            message.getCallId();
            message.getErrorURI();
            message.getErrorDesc();
            message.getErrorDetails();
        }};
    }

    @Test
    public void serializeCanSkipErrorDetails(final CallErrorMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getCallId(); result = "callId";
            message.getErrorURI(); result = "uri";
            message.getErrorDesc(); result = "desc";
            message.getErrorDetails(); result = null;
        }};

        Assert.assertEquals(
            "[" + Message.CALL_ERROR + ",\"callId\",\"uri\",\"desc\"]",
            this.converter.serialize(message, socket)
        );
    }

    @Test
    public void serializeWithErrorDetails(final CallErrorMessage message, final Websocket socket)
        throws InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            message.getCallId(); result = "callId";
            message.getErrorURI(); result = "uri";
            message.getErrorDesc(); result = "desc";
            message.getErrorDetails(); result = 5;
        }};

        Assert.assertEquals(
            "[" + Message.CALL_ERROR + ",\"callId\",\"uri\",\"desc\",5]",
            this.converter.serialize(message, socket)
        );
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenFieldsAreMissing(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize("[" + Message.CALL_ERROR + ", \"callId\", \"uri\"]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenTooManyFields(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize(
            "[" + Message.CALL_ERROR + ", \"callId\", \"uri\", \"desc\", \"details\", \"add\"]",
            socket
        );
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenCallIdIsNoString(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize("[" + Message.CALL_ERROR + ", null, \"uri\", \"desc\"]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenErrorURIIsNoString(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize("[" + Message.CALL_ERROR + ", \"callId\", 5, \"desc\"]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenErrorDescIsNoString(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize("[" + Message.CALL_ERROR + ", \"callId\", \"uri\", 5]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsExceptionWhenErrorDetailsIsPresentButNull(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize("[" + Message.CALL_ERROR + ", \"callId\", \"uri\", \"desc\", null]", socket);
    }

    @Test
    public void deserializeDelegatesCURIInflationToSocket(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        this.converter.deserialize("[" + Message.CALL_ERROR + ", \"callId\", \"uri\", \"desc\"]", socket);

        new Verifications() {{
            socket.inflateCURI("uri");
        }};
    }

    @Test
    public void deserializedMessageHasCallIdOfInput(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        new NonStrictExpectations() {{
            socket.inflateCURI("uri"); result = "uri";
        }};
        CallErrorMessage result = this.converter.deserialize(
            "[" + Message.CALL_ERROR + ", \"callId\", \"uri\", \"desc\"]",
            socket
        );

        Assert.assertEquals("callId", result.getCallId());
    }

    @Test
    public void deserializedMessageHasURIOfInput(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        new NonStrictExpectations() {{
            socket.inflateCURI("uri"); result = "uri";
        }};
        CallErrorMessage result = this.converter.deserialize(
            "[" + Message.CALL_ERROR + ", \"callId\", \"uri\", \"desc\"]",
            socket
        );

        Assert.assertEquals("uri", result.getErrorURI());
    }

    @Test
    public void deserializedMessageHasDescriptionOfInput(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        new NonStrictExpectations() {{
            socket.inflateCURI("uri"); result = "uri";
        }};
        CallErrorMessage result = this.converter.deserialize(
            "[" + Message.CALL_ERROR + ", \"callId\", \"uri\", \"desc\"]",
            socket
        );

        Assert.assertEquals("desc", result.getErrorDesc());
    }

    @Test
    public void deserializedMessageErrorDetailsAreNullIfNotPresent(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        new NonStrictExpectations() {{
            socket.inflateCURI("uri"); result = "uri";
        }};
        CallErrorMessage result = this.converter.deserialize(
            "[" + Message.CALL_ERROR + ", \"callId\", \"uri\", \"desc\"]",
            socket
        );

        Assert.assertNull(result.getErrorDetails());
    }

    @Test
    public void deserializedMessageErrorDetailsAreNotNullIfPresent(final Websocket socket)
        throws InvalidMessageCodeException, MessageParseException
    {
        new NonStrictExpectations() {{
            socket.inflateCURI("uri"); result = "uri";
        }};
        CallErrorMessage result = this.converter.deserialize(
            "[" + Message.CALL_ERROR + ", \"callId\", \"uri\", \"desc\", \"details\"]",
            socket
        );

        Assert.assertNotNull(result.getErrorDetails());
    }
}
