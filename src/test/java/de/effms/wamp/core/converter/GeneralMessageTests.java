package de.effms.wamp.core.converter;

import de.effms.wamp.core.Websocket;
import de.effms.wamp.core.message.Message;
import org.junit.Test;

import java.io.IOException;

abstract public class GeneralMessageTests<T extends Message>
{
    protected JsonParsingConverter<T> converter;

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenMessageIsNoArray(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("message", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenThereIsNoMessageCodeField(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenMessageCodeIsNoNumber(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[null]", socket);
    }

    @Test(expected = MessageParseException.class)
    public void deserializeThrowsMessageParseExceptionWhenMessageCodeIsNoInteger(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[0.5f]", socket);
    }

    @Test(expected = InvalidMessageCodeException.class)
    public void deserializeThrowsInvalidMessageCodeExceptionWhenWrongMessageCode(final Websocket socket)
        throws IOException, MessageParseException, InvalidMessageCodeException
    {
        this.converter.deserialize("[" + Message.INVALID + "]", socket);
    }
}
