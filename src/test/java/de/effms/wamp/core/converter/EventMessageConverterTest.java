package de.effms.wamp.core.converter;

import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.message.EventMessage;
import de.effms.wamp.core.message.Message;
import mockit.Expectations;
import mockit.NonStrictExpectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;

public class EventMessageConverterTest extends GeneralMessageTests<EventMessage>
{
    @Before
    public void setUp()
    {
        this.converter = new EventMessageConverter();
    }

    @Test
    public void canConvertEventMessages()
    {
        Assert.assertTrue(this.converter.canConvert(Message.EVENT));
    }

    @Test
    public void canNotConvertOtherMessage()
    {
        Assert.assertFalse(this.converter.canConvert(Message.CALL_ERROR));
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenTopicFieldIsNotPresent(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.EVENT + "]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenTopicFieldIsEmpty(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.EVENT + ", \"\"]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenPayloadFieldIsMissing(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.EVENT + ", \"myEvent\"]", socket);
    }

    @Test
    public void deserializeUsesEventTopicOfMessage(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            socket.inflateCURI("http://effms.de/myEvent"); result = "http://effms.de/myEvent";
        }};
        EventMessage message = this.converter.deserialize("[" + Message.EVENT + ", \"http://effms.de/myEvent\", null]", socket);

        Assert.assertEquals("http://effms.de/myEvent", message.getTopicURI());
    }

    @Test
    public void deserializeUsesSocketCURIInflation(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        new Expectations() {{
            socket.inflateCURI("prefix:myEvent"); result = "http://effms.de/myEvent";
        }};

        this.converter.deserialize("[" + Message.EVENT + ", \"prefix:myEvent\", null]", socket);
    }

    @Test
    public void deserializeEventTopicIsInflatedCURI(final Websocket socket)
            throws IOException, MessageParseException, InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            socket.inflateCURI("prefix:myEvent"); result = "http://effms.de/myEvent";
        }};
        EventMessage message = this.converter.deserialize("[" + Message.EVENT + ", \"prefix:myEvent\", null]", socket);

        Assert.assertEquals("http://effms.de/myEvent", message.getTopicURI());
    }

    @Test
    public void deserializeEventPayloadIsNotNullButAnyObject(final Websocket socket)
            throws IOException, MessageParseException, InvalidMessageCodeException
    {
        new NonStrictExpectations() {{
            socket.inflateCURI("prefix:myEvent"); result = "http://effms.de/myEvent";
        }};
        EventMessage message = this.converter.deserialize("[" + Message.EVENT + ", \"prefix:myEvent\", null]", socket);

        Assert.assertNotNull(message.getPayload());
    }

    @Test
    public void serializeReturnsCorrectJsonRepresentation(final Websocket socket) throws InvalidMessageCodeException
    {
        EventMessage message = new EventMessage("http://effms.de", "payload");

        Assert.assertEquals(
            "[" + Message.EVENT + ",\"http://effms.de\",\"payload\"]",
            this.converter.serialize(message, socket)
        );
    }
}
