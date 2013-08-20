package de.innoaccel.wamp.server.converter;

import de.innoaccel.wamp.server.Websocket;
import de.innoaccel.wamp.server.message.EventMessage;
import de.innoaccel.wamp.server.message.Message;
import mockit.Expectations;
import mockit.NonStrictExpectations;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;

public class EventMessageConverterTest
{
    private EventMessageConverter converter;

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
}
